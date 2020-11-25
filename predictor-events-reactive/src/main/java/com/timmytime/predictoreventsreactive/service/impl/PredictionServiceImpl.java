package com.timmytime.predictoreventsreactive.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictoreventsreactive.cache.ReceiptCache;
import com.timmytime.predictoreventsreactive.enumerator.CountryCompetitions;
import com.timmytime.predictoreventsreactive.enumerator.Predictions;
import com.timmytime.predictoreventsreactive.facade.WebClientFacade;
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
import java.util.stream.Stream;

@Service("predictionService")
public class PredictionServiceImpl implements PredictionService {

    private static final Logger log = LoggerFactory.getLogger(PredictionServiceImpl.class);

    private final EventService eventService;
    private final TensorflowPredictionService tensorflowPredictionService;
    private final EventOutcomeService eventOutcomeService;

    @Autowired
    public PredictionServiceImpl(
            EventService eventService,
            TensorflowPredictionService tensorflowPredictionService,
            EventOutcomeService eventOutcomeService
    ){
        this.eventService = eventService;
        this.tensorflowPredictionService = tensorflowPredictionService;
        this.eventOutcomeService = eventOutcomeService;
    }

    @Override
    public void start(String country) {
        log.info("starting predictions for {}", country);
        Flux.fromStream(
                CountryCompetitions.valueOf(country).getCompetitions().stream()
        )
                .delayElements(Duration.ofMinutes(1))
                .subscribe(competition ->
                    eventService.getEvents(competition)
                            .subscribe(event ->
                                    Flux.fromArray(Predictions.values())
                                            .limitRate(1)
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
    public Mono<Void> fix() {
        retryMissing();
        return Mono.empty();
    }


    @Override
    public void retryMissing(){
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


}
