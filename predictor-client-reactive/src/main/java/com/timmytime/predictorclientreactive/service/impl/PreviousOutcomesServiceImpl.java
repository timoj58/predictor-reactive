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
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service("previousOutcomesService")
public class PreviousOutcomesServiceImpl implements ILoadService {

    private final Logger log = LoggerFactory.getLogger(PreviousOutcomesServiceImpl.class);

    private final WebClientFacade webClientFacade;
    private final S3Facade s3Facade;
    private final TeamService teamService;
    private final ShutdownService shutdownService;

    private final Map<String, List<UUID>> teamsByCountry = new HashMap<>();

    private final String eventsHost;
    private final String dataHost;

    private final Integer delay;

    @Autowired
    public PreviousOutcomesServiceImpl(
            @Value("${event.host}") String eventsHost,
            @Value("${data.host}") String dataHost,
            @Value("${delay}") Integer delay,
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

        Arrays.asList(
                CountryCompetitions.values()
        ).stream().forEach(country -> {
                    teamsByCountry.put(country.name().toLowerCase(), new ArrayList<>());
                    teamService.get(country.name().toLowerCase())
                            .stream()
                            .forEach(team -> teamsByCountry.get(country.name().toLowerCase()).add(team.getId()));
                }
        );

        teamsByCountry.keySet().stream().forEach(key -> log.info("added {}", key));
    }


    @Override
    public void load() {

        Flux.fromStream(
                Arrays.asList(
                        CountryCompetitions.values()
                ).stream()
        ).delayElements(Duration.ofSeconds(delay * 20))
                .subscribe(country ->
                        Flux.fromStream(
                                teamService.get(country.name().toLowerCase()).stream()
                        ).delayElements(Duration.ofSeconds(delay))
                                .subscribe(
                                        team -> {
                                            log.info("processing {}", team.getLabel());
                                            webClientFacade.getPreviousEventOutcomesByTeam(eventsHost + "/previous-events-by-team/" + team.getId())
                                                    .delayElements(Duration.ofMillis(100))
                                                    .doOnNext(outcome -> saveOutcome(team, outcome))
                                                    .doFinally(finish -> finish(country.name().toLowerCase(), team.getId()))
                                                    .subscribe();
                                        }

                                )
                );

    }

    private void saveOutcome(Team team, EventOutcome eventOutcome) {

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
            JSONObject json = new JSONObject(eventOutcome.getPrediction());
        } catch (Exception e) {
            log.info("{}", eventOutcome.getPrediction());
            eventOutcome.setPrediction(
                    new JSONObject().put("result", new JSONArray(eventOutcome.getPrediction()))
                            .toString()
            );
        }

        predictionOutcomeResponse.setPredictions(eventOutcome.getPrediction());
        predictionOutcomeResponse.setOutcome(eventOutcome.getSuccess());
        //oops need to get score then save....
        webClientFacade.getMatch(getMatchUrl(eventOutcome))
                .subscribe(match -> {
                    try {
                        predictionOutcomeResponse.setScore(match.getHomeScore() + " - " + match.getAwayScore());
                        s3Facade.put(
                                "previous-events/"
                                        + team.getCompetition()
                                        + "/" + team.getId() + "/"
                                        + eventOutcome.getEventType() +
                                        "/" + eventOutcome.getId(),
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

    private void finish(String country, UUID team) {
        log.info("called finish {} {}", country, team);
        /* doesnt ducking work.. FIX ME and add a test it.  its odd.  clearly the key exists.
        teamsByCountry.get(country).remove(team);

        if(teamsByCountry.get(country).isEmpty()){
            log.info("completed {}", country);
            teamsByCountry.remove(country);
        }

        if(teamsByCountry.isEmpty()){
            log.info("completed all countries");
            shutdownService.receive(PreviousOutcomesServiceImpl.class.getName());
        } */
    }
}
