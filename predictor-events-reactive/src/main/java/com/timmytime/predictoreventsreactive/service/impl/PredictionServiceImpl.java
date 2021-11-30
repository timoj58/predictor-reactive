package com.timmytime.predictoreventsreactive.service.impl;

import com.timmytime.predictoreventsreactive.enumerator.CountryCompetitions;
import com.timmytime.predictoreventsreactive.enumerator.Predictions;
import com.timmytime.predictoreventsreactive.model.EventOutcome;
import com.timmytime.predictoreventsreactive.request.Prediction;
import com.timmytime.predictoreventsreactive.request.TensorflowPrediction;
import com.timmytime.predictoreventsreactive.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;
import java.util.function.Consumer;

import static java.time.Duration.ofSeconds;
import static reactor.core.publisher.Flux.fromArray;
import static reactor.core.publisher.Flux.fromStream;

@Slf4j
@Service("predictionService")
public class PredictionServiceImpl implements PredictionService {


    private final EventService eventService;
    private final EventOutcomeService eventOutcomeService;
    private final PredictionMonitorService predictionMonitorService;

    private Consumer<EventOutcome> consumer;

    @Autowired
    public PredictionServiceImpl(
            EventService eventService,
            PredictionMonitorService predictionMonitorService,
            EventOutcomeService eventOutcomeService
    ) {
        this.eventService = eventService;
        this.predictionMonitorService = predictionMonitorService;
        this.eventOutcomeService = eventOutcomeService;

        Flux<EventOutcome> receiver = Flux.create(sink -> consumer = sink::next, FluxSink.OverflowStrategy.BUFFER);
        receiver.limitRate(10).subscribe(this::process);
    }

    @Override
    public void start(String country) {
        log.info("starting predictions for {}", country);
        fromStream(
                CountryCompetitions.valueOf(country).getCompetitions().stream()
        )
                .subscribe(competition ->
                        eventService.getEvents(competition)
                                .subscribe(event ->
                                        fromArray(Predictions.values())
                                                .subscribe(predict ->
                                                        consumer.accept(EventOutcome.builder()
                                                                .id(UUID.randomUUID())
                                                                .home(event.getHome())
                                                                .away(event.getAway())
                                                                .date(event.getDate())
                                                                .competition(event.getCompetition())
                                                                .eventType(predict.name())
                                                                .build())

                                                )
                                )
                );

    }


    private void process(EventOutcome eventOutcome){
        eventOutcomeService.save(eventOutcome).subscribe(saved ->
                predictionMonitorService.addPrediction(
                        TensorflowPrediction.builder()
                         .predictions(Predictions.valueOf(saved.getEventType()))
                        .country(saved.getCountry())
                        .prediction(new Prediction(saved))
                        .build()
                )
        );
    }
}
