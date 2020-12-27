package com.timmytime.predictoreventsreactive.service.impl;

import com.timmytime.predictoreventsreactive.enumerator.CountryCompetitions;
import com.timmytime.predictoreventsreactive.enumerator.Predictions;
import com.timmytime.predictoreventsreactive.model.EventOutcome;
import com.timmytime.predictoreventsreactive.request.Prediction;
import com.timmytime.predictoreventsreactive.request.TensorflowPrediction;
import com.timmytime.predictoreventsreactive.service.EventOutcomeService;
import com.timmytime.predictoreventsreactive.service.EventService;
import com.timmytime.predictoreventsreactive.service.PredictionService;
import com.timmytime.predictoreventsreactive.service.TensorflowPredictionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service("predictionService")
public class PredictionServiceImpl implements PredictionService {


    private final EventService eventService;
    private final TensorflowPredictionService tensorflowPredictionService;
    private final EventOutcomeService eventOutcomeService;

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
        reProcess();
        return Mono.empty();
    }


    @Override
    public void reProcess() {
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

    @Override
    public Mono<Long> outstanding() {
        return eventOutcomeService.toFix().count();
    }


}
