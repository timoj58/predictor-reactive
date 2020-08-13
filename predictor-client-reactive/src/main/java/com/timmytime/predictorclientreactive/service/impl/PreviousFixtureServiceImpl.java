package com.timmytime.predictorclientreactive.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictorclientreactive.facade.S3Facade;
import com.timmytime.predictorclientreactive.facade.WebClientFacade;
import com.timmytime.predictorclientreactive.model.EventOutcome;
import com.timmytime.predictorclientreactive.model.PreviousFixtureResponse;
import com.timmytime.predictorclientreactive.service.ILoadService;
import com.timmytime.predictorclientreactive.service.ShutdownService;
import com.timmytime.predictorclientreactive.service.TeamService;
import com.timmytime.predictorclientreactive.util.CountryCompetitions;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service("previousFixtureService")
public class PreviousFixtureServiceImpl implements ILoadService {

    private final Logger log = LoggerFactory.getLogger(PreviousFixtureServiceImpl.class);

    private final S3Facade s3Facade;
    private final WebClientFacade webClientFacade;
    private final ShutdownService shutdownService;
    private final TeamService teamService;

    private final String eventsHost;
    private final String dataHost;
    private final Integer delay;

    private final Map<String, List<PreviousFixtureResponse>> byCompetition = new HashMap<>();


    @Autowired
    public PreviousFixtureServiceImpl(
            @Value("${}") String eventsHost,
            @Value("${}") String dataHost,
            @Value("${}") Integer delay,
            S3Facade s3Facade,
            WebClientFacade webClientFacade,
            ShutdownService shutdownService,
            TeamService teamService
    ) {
        this.eventsHost = eventsHost;
        this.dataHost = dataHost;
        this.delay = delay;
        this.s3Facade = s3Facade;
        this.webClientFacade = webClientFacade;
        this.shutdownService = shutdownService;
        this.teamService = teamService;
    }

    @Override
    public void load() {

        Flux.fromStream(
                Arrays.asList(
                        CountryCompetitions.values()
                ).stream()
        ).subscribe(country ->
                Flux.fromStream(
                        country.getCompetitions().stream()
                )
                        .subscribe(competition -> {
                            log.info("processing {}", competition);
                            byCompetition.put(competition, new ArrayList<>());
                            List<EventOutcome> eventOutcomes = new ArrayList<>();
                            webClientFacade.getPreviousEvents(eventsHost+"/previous-events/"+competition)
                                    .doOnNext(event -> eventOutcomes.add(event))
                                    .doFinally(transform ->
                                            Flux.fromStream(
                                                    eventOutcomes.stream()
                                            ).doOnNext(event ->
                                                            webClientFacade.getMatch(getMatchUrl(event))
                                                                    .subscribe(match -> byCompetition.get(competition).add(transform(event).withScore(match)))
                                                    )
                                            .doFinally(save ->
                                                    Mono.just(competition).delayElement(Duration.ofMinutes(delay)).subscribe(key -> save(key)))
                                            .subscribe()
                                    ).subscribe();
                        }));

    }

    private void save(String competition){
        log.info("saving {}", competition);
        try {
            s3Facade.put("previous-fixtures/"+competition,
                    new ObjectMapper().writeValueAsString(
                            byCompetition.get(competition)
                    ));
            byCompetition.remove(competition);

            if(byCompetition.keySet().isEmpty()){
                log.info("completed all competitions");
                shutdownService.receive(PreviousFixtureServiceImpl.class.getName());
            }

        } catch (JsonProcessingException e) {
            log.error("json processing error");
        }
    }

    private String getMatchUrl(EventOutcome eventOutcome){
       return dataHost+"/match?home="
                +eventOutcome.getHome()+"&away="
                +eventOutcome.getAway()
                +"&date="+ eventOutcome.getDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }



    private PreviousFixtureResponse transform(EventOutcome event) {
        PreviousFixtureResponse previousFixtureResponse = new PreviousFixtureResponse();

        previousFixtureResponse.setHome(teamService.getTeam(event.getCountry(), event.getHome()));
        previousFixtureResponse.setAway(teamService.getTeam(event.getCountry(), event.getAway()));

        previousFixtureResponse.setEventDate(event.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        previousFixtureResponse.setEventType(event.getEventType());
        previousFixtureResponse.setSuccess(event.getSuccess());
        previousFixtureResponse.setPredictions(event.getPrediction());

        return previousFixtureResponse;
    }

}
