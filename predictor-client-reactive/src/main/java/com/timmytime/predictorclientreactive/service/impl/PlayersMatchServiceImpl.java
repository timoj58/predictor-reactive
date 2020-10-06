package com.timmytime.predictorclientreactive.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictorclientreactive.enumerator.Competition;
import com.timmytime.predictorclientreactive.enumerator.CountryCompetitions;
import com.timmytime.predictorclientreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorclientreactive.facade.S3Facade;
import com.timmytime.predictorclientreactive.facade.WebClientFacade;
import com.timmytime.predictorclientreactive.model.*;
import com.timmytime.predictorclientreactive.service.ILoadService;
import com.timmytime.predictorclientreactive.service.ShutdownService;
import com.timmytime.predictorclientreactive.service.TeamService;
import com.timmytime.predictorclientreactive.util.MatchSelectionResponseTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;
import java.util.function.BiFunction;

@Service("playersMatchService")
public class PlayersMatchServiceImpl implements ILoadService {

    private final Logger log = LoggerFactory.getLogger(PlayersMatchServiceImpl.class);

    private final S3Facade s3Facade;
    private final WebClientFacade webClientFacade;
    private final ShutdownService shutdownService;

    private final Integer delay;
    private final String playersHost;
    private final String eventDataHost;

    private final Map<Competition, List<MatchSelectionsResponse>> byCompetition = new HashMap<>();
    private final MatchSelectionResponseTransformer matchSelectionResponseTransformer = new MatchSelectionResponseTransformer();

    private BiFunction<List<MatchSelectionsResponse>, FantasyEventTypes, List<PlayerResponse>> process = (matchSelectionsResponses, fantasyEventTypes) ->
            matchSelectionsResponses
                    .stream()
                    .map(m -> m.getMatchSelectionResponses())
                    .flatMap(List::stream)
                    .filter(f -> f.getEvent().equals(fantasyEventTypes.name().toLowerCase()))
                    .findFirst()
                    .orElse(new MatchSelectionResponse()).getPlayerResponses();


    @Autowired
    public PlayersMatchServiceImpl(
            @Value("${event.data.host}") String eventDataHost,
            @Value("${players.host}") String playersHost,
            @Value("${delay}") Integer delay,
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

        Flux.fromStream(
                Arrays.asList(
                        Competition.values()
                ).stream().filter(f -> f.getFantasyLeague() == Boolean.TRUE)
        ).subscribe(
                league -> create(league)
        );

    }

    private void create(Competition league) {
        byCompetition.put(league, new ArrayList<>());

        webClientFacade.getUpcomingEvents(eventDataHost + "/events/" + league.name().toLowerCase())
                .doOnNext(event -> {
                    log.info("processing {} vs {}", event.getHome(), event.getAway());
                    List<PlayerResponse> playerResponses = new ArrayList<>();
                    webClientFacade.getPlayers(playersHost+"/players/match/"+league+"?home="+event.getHome()+"&away="+event.getAway()) //need players by teams...
                            .doOnNext(player ->
                                    webClientFacade.getPlayer(playersHost+"//player/"+player.getId()) //get player response
                                    .subscribe(playerResponse -> playerResponses.add(playerResponse))
                            ) //get the appearance and stats set up, create a player response
                            .doFinally(match -> Mono.just(playerResponses)
                                    .delayElement(Duration.ofMinutes(delay))
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
                .doFinally(save -> Mono.just(league).delayElement(Duration.ofMinutes(delay*2)).subscribe(key -> save(key)))
                .subscribe();

    }

    private void save(Competition competition) {
        log.info("saving {}", competition);

        byCompetition.get(competition)
                .stream()
                .forEach(event -> {
                    try {
                        s3Facade.put("player-events/"+competition,
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
        TopSelectionsResponse topSelectionsSavesResponse = new TopSelectionsResponse(FantasyEventTypes.SAVES, new ArrayList<>());
        TopSelectionsResponse topSelectionsYellowsResponse = new TopSelectionsResponse(FantasyEventTypes.YELLOW_CARD, new ArrayList<>());

        topSelectionsGoalsResponse.process(process.apply(byCompetition.get(competition), FantasyEventTypes.GOALS));
        topSelectionsAssistsResponse.process(process.apply(byCompetition.get(competition), FantasyEventTypes.ASSISTS));
        topSelectionsSavesResponse.process(process.apply(byCompetition.get(competition), FantasyEventTypes.SAVES));
        topSelectionsYellowsResponse.process(process.apply(byCompetition.get(competition), FantasyEventTypes.YELLOW_CARD));

        Arrays.asList(topSelectionsAssistsResponse, topSelectionsGoalsResponse, topSelectionsSavesResponse, topSelectionsYellowsResponse)
                .stream()
                .forEach(topSelectionsResponse ->
        {
            try {
                s3Facade.put("top-performers/"+competition,
                        new ObjectMapper().writeValueAsString(
                                topSelectionsGoalsResponse
                        ));
            } catch (JsonProcessingException e) {
                log.error("json", e);
            }
        });

        byCompetition.remove(competition);

        if (byCompetition.keySet().isEmpty()) {
            log.info("completed all competitions");
            shutdownService.receive(TeamsMatchServiceImpl.class.getName());
        }


    }

}
