package com.timmytime.predictorclientreactive.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictorclientreactive.enumerator.Competition;
import com.timmytime.predictorclientreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorclientreactive.facade.S3Facade;
import com.timmytime.predictorclientreactive.facade.WebClientFacade;
import com.timmytime.predictorclientreactive.model.MatchSelectionResponse;
import com.timmytime.predictorclientreactive.model.MatchSelectionsResponse;
import com.timmytime.predictorclientreactive.model.PlayerResponse;
import com.timmytime.predictorclientreactive.model.TopSelectionsResponse;
import com.timmytime.predictorclientreactive.service.ILoadService;
import com.timmytime.predictorclientreactive.service.ShutdownService;
import com.timmytime.predictorclientreactive.util.MatchSelectionResponseTransformer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static java.time.Duration.ofMinutes;
import static java.util.Arrays.asList;
import static java.util.stream.Stream.of;
import static reactor.core.publisher.Flux.fromStream;
import static reactor.core.publisher.Mono.just;

@Slf4j
@Service("playersMatchService")
public class PlayersMatchServiceImpl implements ILoadService {

    private final S3Facade s3Facade;
    private final WebClientFacade webClientFacade;
    private final ShutdownService shutdownService;

    private final Integer delay;
    private final String playersHost;
    private final String eventDataHost;

    private final Map<Competition, List<MatchSelectionsResponse>> byCompetition = new HashMap<>();
    private final MatchSelectionResponseTransformer matchSelectionResponseTransformer = new MatchSelectionResponseTransformer();

    private final BiFunction<List<MatchSelectionsResponse>, FantasyEventTypes, List<PlayerResponse>> process = (matchSelectionsResponses, fantasyEventTypes) ->
            matchSelectionsResponses
                    .stream()
                    .map(MatchSelectionsResponse::getMatchSelectionResponses)
                    .flatMap(List::stream)
                    .filter(f -> f.getEvent().equals(fantasyEventTypes.name().toLowerCase()))
                    .map(MatchSelectionResponse::getPlayerResponses)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());


    @Autowired
    public PlayersMatchServiceImpl(
            @Value("${clients.event-data}") String eventDataHost,
            @Value("${clients.player-events}") String playersHost,
            @Value("${delays.delay}") Integer delay,
            S3Facade s3Facade,
            WebClientFacade webClientFacade,
            ShutdownService shutdownService
    ) {
        this.eventDataHost = eventDataHost;
        this.playersHost = playersHost;
        this.delay = delay;
        this.s3Facade = s3Facade;
        this.webClientFacade = webClientFacade;
        this.shutdownService = shutdownService;
    }


    @Override
    public void load() {
        fromStream(
                of(Competition.values())
                        .filter(f -> f.getFantasyLeague() == Boolean.TRUE)
        ).subscribe(this::create);
    }

    private void create(Competition league) {
        byCompetition.put(league, new ArrayList<>());

        webClientFacade.getUpcomingEvents(eventDataHost + "/events/" + league.name().toLowerCase())
                .doOnNext(event -> {
                    log.info("processing {} vs {}", event.getHome(), event.getAway());
                    List<PlayerResponse> playerResponses = new ArrayList<>();
                    webClientFacade.getPlayers(playersHost + "/players/match/" + league + "?home=" + event.getHome() + "&away=" + event.getAway()) //need players by teams...
                            .doOnNext(player ->
                                    webClientFacade.getPlayer(playersHost + "/player/" + player.getId()) //get player response
                                            .subscribe(playerResponses::add)
                            ) //get the appearance and stats set up, create a player response
                            .doFinally(match -> just(playerResponses)
                                    .delayElement(ofMinutes(delay))
                                    .subscribe(players -> {
                                        MatchSelectionsResponse matchSelectionsResponse
                                                = new MatchSelectionsResponse(
                                                event.getHome(),
                                                event.getAway(),
                                                matchSelectionResponseTransformer.transform(players)
                                        );

                                        byCompetition.get(league).add(matchSelectionsResponse);
                                    })
                            ).subscribe();
                })
                .doFinally(save -> just(league).delayElement(ofMinutes(delay * 2)).subscribe(this::save))
                .subscribe();

    }

    private void save(Competition competition) {
        log.info("saving {}", competition);

        byCompetition.get(competition)
                .forEach(event -> {
                    try {
                        s3Facade.put("player-events/" + competition + "/" + event.getHome() + "/" + event.getAway(),
                                new ObjectMapper().writeValueAsString(
                                        event
                                ));

                    } catch (JsonProcessingException e) {
                        log.error("json processing error");
                    }
                });

        saveTopSelections(competition);
    }

    private void saveTopSelections(Competition competition) {
        TopSelectionsResponse topSelectionsGoalsResponse = new TopSelectionsResponse(FantasyEventTypes.GOALS, new ArrayList<>()); //should be using enums...
        TopSelectionsResponse topSelectionsAssistsResponse = new TopSelectionsResponse(FantasyEventTypes.ASSISTS, new ArrayList<>());
        TopSelectionsResponse topSelectionsYellowsResponse = new TopSelectionsResponse(FantasyEventTypes.YELLOW_CARD, new ArrayList<>());

        topSelectionsGoalsResponse.process(process.apply(byCompetition.get(competition), FantasyEventTypes.GOALS));
        topSelectionsAssistsResponse.process(process.apply(byCompetition.get(competition), FantasyEventTypes.ASSISTS));
        topSelectionsYellowsResponse.process(process.apply(byCompetition.get(competition), FantasyEventTypes.YELLOW_CARD));

        asList(topSelectionsAssistsResponse, topSelectionsGoalsResponse, topSelectionsYellowsResponse)
                .forEach(topSelectionsResponse ->
                {
                    try {
                        s3Facade.put("top-performers/" + competition + "/" + topSelectionsResponse.getEvent(),
                                new ObjectMapper().writeValueAsString(
                                        topSelectionsResponse
                                ));
                    } catch (JsonProcessingException e) {
                        log.error("json", e);
                    }
                });

        byCompetition.remove(competition);

        if (byCompetition.keySet().isEmpty()) {
            log.info("completed all competitions");
            shutdownService.receive(PlayersMatchServiceImpl.class.getName());
        }


    }

}
