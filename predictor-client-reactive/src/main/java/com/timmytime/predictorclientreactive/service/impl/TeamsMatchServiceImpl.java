package com.timmytime.predictorclientreactive.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictorclientreactive.facade.S3Facade;
import com.timmytime.predictorclientreactive.facade.WebClientFacade;
import com.timmytime.predictorclientreactive.model.EventOutcome;
import com.timmytime.predictorclientreactive.model.EventOutcomeResponse;
import com.timmytime.predictorclientreactive.service.ILoadService;
import com.timmytime.predictorclientreactive.service.ShutdownService;
import com.timmytime.predictorclientreactive.service.TeamService;
import com.timmytime.predictorclientreactive.enumerator.CountryCompetitions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;

@Service("teamsMatchService")
public class TeamsMatchServiceImpl implements ILoadService {

    private final Logger log = LoggerFactory.getLogger(TeamsMatchServiceImpl.class);

    private final WebClientFacade webClientFacade;
    private final S3Facade s3Facade;
    private final ShutdownService shutdownService;
    private final TeamService teamService;

    private final String eventsHost;
    private final Integer delay;

    private final Map<String, List<EventOutcomeResponse>> byCompetition = new HashMap<>();


    @Autowired
    public TeamsMatchServiceImpl(
            @Value("${event.host}") String eventsHost,
            @Value("${delay}") Integer delay,
            WebClientFacade webClientFacade,
            S3Facade s3Facade,
            TeamService teamService,
            ShutdownService shutdownService
    ){
        this.eventsHost = eventsHost;
        this.delay = delay;
        this.webClientFacade = webClientFacade;
        this.s3Facade = s3Facade;
        this.shutdownService = shutdownService;
        this.teamService = teamService;
    }

    @Override
    public void load() {

        Flux.fromStream(
                Arrays.asList(CountryCompetitions.values()).stream()
        ).subscribe(country ->
                Flux.fromStream(
                        country.getCompetitions().stream()
                ).subscribe(
                        competition -> {
                            log.info("processing {}", competition);
                            byCompetition.put(competition, new ArrayList<>());

                            webClientFacade.getUpcomingEventOutcomes(eventsHost+"/events/"+competition)
                                    .doOnNext(event -> byCompetition.get(competition).add(transform(event)))
                                    .doFinally(save ->
                                            Mono.just(competition).delayElement(Duration.ofMinutes(delay)).subscribe(key -> save(key)))
                                    .subscribe();
                        }
                )
        );
    }

    private void save(String competition){
        log.info("saving {}", competition);

            byCompetition.get(competition)
                    .stream()
                    .forEach(event -> {
                        try{
                        s3Facade.put("upcoming-events/"+competition+"/"+event.getHome().getId()+"/"+event.getAway().getId()+"/"+event.getEventType(),
                                new ObjectMapper().writeValueAsString(
                                        event
                                ));

                        } catch (JsonProcessingException e) {
                            log.error("json processing error");
                        }
                    });

            byCompetition.remove(competition);

            if(byCompetition.keySet().isEmpty()){
                log.info("completed all competitions");
                shutdownService.receive(TeamsMatchServiceImpl.class.getName());
            }

    }


    private EventOutcomeResponse transform(EventOutcome event){

            //legacy stuff.
            try{
                JSONObject json = new JSONObject(event.getPrediction());
            }catch (Exception e){
                log.info("{}", event.getPrediction());
                event.setPrediction(
                        new JSONObject().put("result", new JSONArray(event.getPrediction()))
                        .toString()
                );
            }

            EventOutcomeResponse eventOutcomeResponse = new EventOutcomeResponse(event);
            eventOutcomeResponse.setHome(teamService.getTeam(event.getCountry(), event.getHome()));
            eventOutcomeResponse.setAway(teamService.getTeam(event.getCountry(), event.getAway()));

            log.info("processing {}", event.getId());

            return eventOutcomeResponse;

    }
}
