package com.timmytime.predictorclientreactive.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictorclientreactive.facade.S3Facade;
import com.timmytime.predictorclientreactive.facade.WebClientFacade;
import com.timmytime.predictorclientreactive.model.EventOutcome;
import com.timmytime.predictorclientreactive.model.PreviousFixtureOutcome;
import com.timmytime.predictorclientreactive.model.PreviousFixtureResponse;
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
            @Value("${event.host}") String eventsHost,
            @Value("${data.host}") String dataHost,
            @Value("${delay}") Integer delay,
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
                            webClientFacade.getPreviousEventOutcomes(eventsHost+"/previous-events/"+competition)
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
                            normalize(byCompetition.get(competition))
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

    private String legacyShit(EventOutcome eventOutcome){

        try{
            JSONArray results = new JSONObject(eventOutcome.getPrediction()).getJSONArray("result");
            return eventOutcome.getPrediction();
        }catch (Exception e){
            return new JSONObject().put(
                    "result", new JSONArray(eventOutcome.getPrediction())
            ).toString();
        }
    }



    private PreviousFixtureResponse transform(EventOutcome event) {
        PreviousFixtureResponse previousFixtureResponse = new PreviousFixtureResponse();

        previousFixtureResponse.setHome(teamService.getTeam(event.getCountry(), event.getHome()));
        previousFixtureResponse.setAway(teamService.getTeam(event.getCountry(), event.getAway()));

        previousFixtureResponse.setEventDate(event.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        //need to combine them by home / away / date.
        previousFixtureResponse.getPreviousFixtureOutcomes().add(
                PreviousFixtureOutcome.builder()
                        .eventType(event.getEventType())
                        .success(event.getSuccess())
                        .predictions(legacyShit(event))
                        .build()
        );
        return previousFixtureResponse;
    }

    private List<PreviousFixtureResponse> normalize(List<PreviousFixtureResponse> previousFixtureResponses){

        //TODO FIX ME - this fails, mainly as the data is broken

        List<PreviousFixtureResponse> toSave = new ArrayList<>();

        previousFixtureResponses
                .stream()
                .filter(f ->
                        f.getPreviousFixtureOutcomes()
                                .stream()
                                .filter(e -> e.getEventType().equals("PREDICT_RESULTS"))
                        .findFirst().isPresent()
                ).forEach(result -> {

                    result.getPreviousFixtureOutcomes().add(
                          previousFixtureResponses
                                    .stream()
                                  .filter(f -> f.getHome().equals(result.getHome()))
                                  .filter(f -> f.getAway().equals(result.getAway()))
                                  .filter(f -> f.getEventDate().equals(result.getEventDate()))
                                  .filter(f ->
                                            f.getPreviousFixtureOutcomes()
                                                    .stream()
                                                    .filter(e -> e.getEventType().equals("PREDICT_GOALS"))
                                                    .findFirst()
                                            .isPresent())
                            .findFirst().get().getPreviousFixtureOutcomes().stream().findFirst().get()
                    );

                    result.getPreviousFixtureOutcomes().stream()
                            .forEach(type -> type.setTotalGoals(
                                    result.getHomeScore()+result.getAwayScore()
                            ));

                    toSave.add(result);
        });

        return toSave;
    };

}
