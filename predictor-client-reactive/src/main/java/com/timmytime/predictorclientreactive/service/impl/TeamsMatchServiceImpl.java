package com.timmytime.predictorclientreactive.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictorclientreactive.enumerator.CountryCompetitions;
import com.timmytime.predictorclientreactive.facade.S3Facade;
import com.timmytime.predictorclientreactive.facade.WebClientFacade;
import com.timmytime.predictorclientreactive.model.EventOutcome;
import com.timmytime.predictorclientreactive.model.EventOutcomeResponse;
import com.timmytime.predictorclientreactive.service.ILoadService;
import com.timmytime.predictorclientreactive.service.ShutdownService;
import com.timmytime.predictorclientreactive.service.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Stream.of;
import static reactor.core.publisher.Flux.fromStream;
import static reactor.core.publisher.Mono.just;

@Slf4j
@Service("teamsMatchService")
public class TeamsMatchServiceImpl implements ILoadService {

    private final WebClientFacade webClientFacade;
    private final S3Facade s3Facade;
    private final ShutdownService shutdownService;
    private final TeamService teamService;

    private final String eventsHost;
    private final Integer delay;

    private final Map<String, List<EventOutcomeResponse>> byCompetition = new HashMap<>();


    @Autowired
    public TeamsMatchServiceImpl(
            @Value("${clients.events}") String eventsHost,
            @Value("${delays.delay}") Integer delay,
            WebClientFacade webClientFacade,
            S3Facade s3Facade,
            TeamService teamService,
            ShutdownService shutdownService
    ) {
        this.eventsHost = eventsHost;
        this.delay = delay;
        this.webClientFacade = webClientFacade;
        this.s3Facade = s3Facade;
        this.shutdownService = shutdownService;
        this.teamService = teamService;
    }

    @Override
    public void load() {

        fromStream(
                of(CountryCompetitions.values())
        ).subscribe(country ->
                fromStream(
                        country.getCompetitions().stream()
                ).subscribe(
                        competition -> {
                            log.info("processing {}", competition);
                            byCompetition.put(competition, new ArrayList<>());

                            webClientFacade.getUpcomingEventOutcomes(eventsHost + "/events/" + competition)
                                    .doOnNext(event -> byCompetition.get(competition).add(transform(event)))
                                    .doFinally(save ->
                                            just(competition).delayElement(Duration.ofMinutes(delay)).subscribe(this::save))
                                    .subscribe();
                        }
                )
        );
    }

    private void save(String competition) {
        log.info("saving {} {} {}", competition, byCompetition.size(), byCompetition.get(competition).size());

        byCompetition.get(competition)
                .forEach(event -> {
                    try {
                        s3Facade.put("upcoming-events/" + competition + "/" + event.getHome().getId() + "/" + event.getAway().getId() + "/" + event.getEventType(),
                                new ObjectMapper().writeValueAsString(
                                        event
                                ));

                    } catch (JsonProcessingException e) {
                        log.error("json processing error");
                    }
                });

        byCompetition.remove(competition);

        if (byCompetition.keySet().isEmpty()) {
            log.info("completed all competitions");
            shutdownService.receive(TeamsMatchServiceImpl.class.getName());
        }

    }


    private EventOutcomeResponse transform(EventOutcome event) {

        //legacy stuff.
        try {
            JSONObject json = new JSONObject(event.getPrediction());
        } catch (Exception e) {
            log.info("{}", event.getPrediction());
            event.setPrediction(
                    new JSONObject().put("result", new JSONArray(event.getPrediction()))
                            .toString()
            );
        }

        EventOutcomeResponse eventOutcomeResponse = new EventOutcomeResponse(event);
        eventOutcomeResponse.setHome(teamService.getTeam(event.getCountry(), event.getHome()));
        eventOutcomeResponse.setAway(teamService.getTeam(event.getCountry(), event.getAway()));


        return eventOutcomeResponse;

    }
}
