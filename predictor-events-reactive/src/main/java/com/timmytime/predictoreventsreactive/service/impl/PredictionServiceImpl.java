package com.timmytime.predictoreventsreactive.service.impl;

import com.timmytime.predictoreventsreactive.enumerator.CountryCompetitions;
import com.timmytime.predictoreventsreactive.enumerator.Predictions;
import com.timmytime.predictoreventsreactive.model.EventOutcome;
import com.timmytime.predictoreventsreactive.model.PredictionLine;
import com.timmytime.predictoreventsreactive.request.Prediction;
import com.timmytime.predictoreventsreactive.request.TensorflowPrediction;
import com.timmytime.predictoreventsreactive.service.EventOutcomeService;
import com.timmytime.predictoreventsreactive.service.EventService;
import com.timmytime.predictoreventsreactive.service.PredictionService;
import com.timmytime.predictoreventsreactive.service.TensorflowPredictionService;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service("predictionService")
public class PredictionServiceImpl implements PredictionService {

    private static final Logger log = LoggerFactory.getLogger(PredictionServiceImpl.class);

    private final EventService eventService;
    private final TensorflowPredictionService tensorflowPredictionService;
    private final EventOutcomeService eventOutcomeService;
    private final Integer competitionDelay;

    @Autowired
    public PredictionServiceImpl(
            @Value("${competition.delay}") Integer competitionDelay,
            EventService eventService,
            TensorflowPredictionService tensorflowPredictionService,
            EventOutcomeService eventOutcomeService
    ){
        this.competitionDelay = competitionDelay;
        this.eventService = eventService;
        this.tensorflowPredictionService = tensorflowPredictionService;
        this.eventOutcomeService = eventOutcomeService;
        this.tensorflowPredictionService.setReplayConsumer(id -> processFix());
    }

    @Override
    public void start(String country) {
        log.info("starting predictions for {}", country);
        Flux.fromStream(
                CountryCompetitions.valueOf(country).getCompetitions().stream()
        )
                .subscribe(competition ->
                    eventService.getEvents(competition)
                            .subscribe(event ->
                                    Flux.fromStream(
                                            Arrays.asList(Predictions.values()).stream()
                                    )
                                            .subscribe(predict ->
                                                    eventOutcomeService.save(
                                                            EventOutcome.builder()
                                                                    .id(UUID.randomUUID())
                                                                    .home(event.getHome())
                                                                    .away(event.getAway())
                                                                    .date(event.getDate())
                                                                    .competition(event.getCompetition())
                                                                    .eventType(predict.name())
                                                                    .build()
                                                    ).subscribe(eventOutcome ->
                                                            tensorflowPredictionService.predict(
                                                                    TensorflowPrediction.builder()
                                                                            .predictions(predict)
                                                                            .country(eventOutcome.getCountry())
                                                                            .prediction(new Prediction(eventOutcome))
                                                                            .build()
                                                            )
                                                    )
                                            )
                            )
                );

    }

    @Override
    public void result(UUID id, JSONObject result) {
        log.info("received a result {}", id.toString());
        //need to sort out the fix stuff.  to do.  on end of stream i guess. makes more sense...
            eventOutcomeService.find(id)
                    .subscribe(eventOutcome -> {
                        eventOutcome.setPrediction(normalize(result).toString());
                        log.info("saving {}", eventOutcome.getId());
                        eventOutcomeService.save(eventOutcome).subscribe();
                    });
    }

    @Override
    public Mono<Void> fix() {
        processFix();
        return Mono.empty();
    }


    private void processFix(){
        log.info("processing records to fix");
        eventOutcomeService.toFix().subscribe(eventOutcome ->
                tensorflowPredictionService.predict(
                        TensorflowPrediction.builder()
                                .predictions(Predictions.valueOf(eventOutcome.getEventType()))
                                .country(eventOutcome.getCountry())
                                .prediction(new Prediction(eventOutcome))
                                .build()
                )
        );
    }

    private List<PredictionLine> normalize(JSONObject result){
        //get our keys.
        Map<String, List<Double>> byIndex = new HashMap<>();


        Iterator<String> keys = result.keys();
        while (keys.hasNext()) {
            String key = keys.next();

            String keyLabel = result.getJSONObject(key).get("label").toString();
            if (!byIndex.containsKey(keyLabel)) {
                byIndex.put(keyLabel, new ArrayList<>());
            }

            byIndex.get(keyLabel).add(Double.valueOf(result.getJSONObject(key).get("score").toString()));
        }


        List<PredictionLine> normalized = new ArrayList<>();

        byIndex.keySet().stream().forEach(
                key -> normalized.add(
                        new PredictionLine(key,
                                byIndex.get(key)
                                        .stream()
                                        .mapToDouble(m -> m).average().getAsDouble()))
        );

        return normalized
                .stream()
                .sorted((o1, o2) -> o2.getScore().compareTo(o1.getScore()))
                .collect(Collectors.toList());
    }

}
