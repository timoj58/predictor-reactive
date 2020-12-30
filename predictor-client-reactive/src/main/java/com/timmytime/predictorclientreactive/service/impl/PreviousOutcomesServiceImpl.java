package com.timmytime.predictorclientreactive.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictorclientreactive.enumerator.CountryCompetitions;
import com.timmytime.predictorclientreactive.facade.S3Facade;
import com.timmytime.predictorclientreactive.facade.WebClientFacade;
import com.timmytime.predictorclientreactive.model.EventOutcome;
import com.timmytime.predictorclientreactive.model.PredictionOutcomeResponse;
import com.timmytime.predictorclientreactive.model.Team;
import com.timmytime.predictorclientreactive.service.ILoadService;
import com.timmytime.predictorclientreactive.service.ShutdownService;
import com.timmytime.predictorclientreactive.service.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Slf4j
@Service("previousOutcomesService")
public class PreviousOutcomesServiceImpl implements ILoadService {

    private final WebClientFacade webClientFacade;
    private final S3Facade s3Facade;
    private final TeamService teamService;
    private final ShutdownService shutdownService;

    private final Map<String, List<UUID>> teamsByCountry = new HashMap<>();

    private final String eventsHost;
    private final String dataHost;

    private final Integer delay;

    private final Flux<Triple<EventOutcome, PredictionOutcomeResponse, Pair<Integer, Team>>> outcomes;
    private Consumer<Triple<EventOutcome, PredictionOutcomeResponse, Pair<Integer, Team>>> receiver;

    @Autowired
    public PreviousOutcomesServiceImpl(
            @Value("${clients.event}") String eventsHost,
            @Value("${clients.data}") String dataHost,
            @Value("${delays.delay}") Integer delay,
            WebClientFacade webClientFacade,
            S3Facade s3Facade,
            TeamService teamService,
            ShutdownService shutdownService
    ) {
        this.eventsHost = eventsHost;
        this.dataHost = dataHost;
        this.delay = delay;
        this.webClientFacade = webClientFacade;
        this.s3Facade = s3Facade;
        this.teamService = teamService;
        this.shutdownService = shutdownService;

        this.outcomes = Flux.push(sink ->
                PreviousOutcomesServiceImpl.this.receiver = (t) -> sink.next(t), FluxSink.OverflowStrategy.BUFFER);

        this.outcomes.limitRate(1).subscribe(this::saveOutcome);
    }


    @Override
    public void load() {

        //TODO.  needs a test and review why missing some files

        CompletableFuture.runAsync(() -> init())
                .thenRun(() -> Mono.just(CountryCompetitions.values()).delayElement(Duration.ofMinutes(delay * 5)) //takes a while to delete all the files
                        .subscribe(competitions ->

                                Flux.fromArray(competitions)
                                        .delayElements(Duration.ofSeconds(delay * 20))
                                        .subscribe(country ->
                                                Flux.fromStream(
                                                        teamService.get(country.name().toLowerCase()).stream()
                                                ).delayElements(Duration.ofSeconds(delay))
                                                        .subscribe(
                                                                team -> {
                                                                    AtomicInteger index = new AtomicInteger(0);
                                                                    log.info("processing {}", team.getLabel());
                                                                    webClientFacade.getPreviousEventOutcomesByTeam(eventsHost + "/previous-events-by-team/" + team.getId())
                                                                            .sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()))
                                                                            .doOnNext(outcome -> createOutcome(team, outcome, index.getAndIncrement()))
                                                                            .doFinally(finish -> finish(country.name().toLowerCase(), team.getId()))
                                                                            .subscribe();
                                                                }

                                                        ))
                        )
                );

    }

    private void createOutcome(Team team, EventOutcome eventOutcome, Integer index) {

        log.info("index {}", index);

        PredictionOutcomeResponse predictionOutcomeResponse = new PredictionOutcomeResponse();

        predictionOutcomeResponse.setHome(
                teamService.getTeam(team.getCountry(), eventOutcome.getHome()).getLabel()
        );
        predictionOutcomeResponse.setAway(
                teamService.getTeam(team.getCountry(), eventOutcome.getAway()).getLabel()
        );

        predictionOutcomeResponse.setEventDate(
                eventOutcome.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        );

        try {
            var json = new JSONObject(eventOutcome.getPrediction());
        } catch (Exception e) {
            log.info("{}", eventOutcome.getPrediction());
            eventOutcome.setPrediction(
                    new JSONObject().put("result", new JSONArray(eventOutcome.getPrediction()))
                            .toString()
            );
        }

        predictionOutcomeResponse.setPredictions(eventOutcome.getPrediction());
        predictionOutcomeResponse.setOutcome(eventOutcome.getSuccess());

        receiver.accept(Triple.of(eventOutcome, predictionOutcomeResponse, Pair.of(index, team)));
    }

    private void saveOutcome(Triple<EventOutcome, PredictionOutcomeResponse, Pair<Integer, Team>> data) {

        var predictionOutcomeResponse = data.getMiddle();
        var eventOutcome = data.getLeft();
        var index = data.getRight().getLeft();
        var team = data.getRight().getRight();

        webClientFacade.getMatch(getMatchUrl(eventOutcome))
                .subscribe(match -> {
                    try {
                        predictionOutcomeResponse.setScore(match.getHomeScore() + " - " + match.getAwayScore());
                        s3Facade.put(
                                "previous-events/"
                                        + team.getCompetition() + "/"
                                        + team.getId() + "/"
                                        + eventOutcome.getEventType() + "/"
                                        + index + "/"
                                        + eventOutcome.getId(),
                                new ObjectMapper().writeValueAsString(predictionOutcomeResponse)
                        );
                    } catch (JsonProcessingException e) {
                        log.error("json", e);
                    }
                });
    }

    private String getMatchUrl(EventOutcome eventOutcome) {
        return dataHost + "/match?home="
                + eventOutcome.getHome() + "&away="
                + eventOutcome.getAway()
                + "&date=" + eventOutcome.getDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    private void init() {
        Flux.fromArray(
                CountryCompetitions.values()
        ).subscribe(country -> {
                    teamsByCountry.put(country.name().toLowerCase(), new ArrayList<>());
                    teamService.get(country.name().toLowerCase())
                            .stream()
                            .forEach(team -> {
                                log.info("processing {}", team.getLabel());
                                teamsByCountry.get(country.name().toLowerCase()).add(team.getId());
                                s3Facade.delete("previous-events/" + team.getCompetition() + "/" + team.getId() + "/");
                            });
                }
        );
    }

    private void finish(String country, UUID team) {
        log.info("called finish {} {}", country, team);
        teamsByCountry.get(country).remove(team);

        if (teamsByCountry.get(country).isEmpty()) {
            log.info("completed {}", country);
            teamsByCountry.remove(country);
        }

        if (teamsByCountry.isEmpty()) {
            log.info("completed all countries");
            Mono.just(PreviousOutcomesServiceImpl.class.getName())
                    .delayElement(Duration.ofMinutes(1))
                    .subscribe(shutdownService::receive);
        }
    }
}
