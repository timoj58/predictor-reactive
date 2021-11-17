package com.timmytime.predictoreventsreactive.service.impl;

import com.timmytime.predictoreventsreactive.enumerator.Predictions;
import com.timmytime.predictoreventsreactive.facade.WebClientFacade;
import com.timmytime.predictoreventsreactive.model.Event;
import com.timmytime.predictoreventsreactive.model.EventOutcome;
import com.timmytime.predictoreventsreactive.service.EventOutcomeService;
import com.timmytime.predictoreventsreactive.service.EventService;
import com.timmytime.predictoreventsreactive.service.PredictionService;
import com.timmytime.predictoreventsreactive.service.TensorflowPredictionService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.Mockito.*;

class PredictionServiceImplTest {

    private static final EventOutcomeService eventOutcomeService = mock(EventOutcomeService.class);
    private final EventService eventService = mock(EventService.class);
    private final TensorflowPredictionService tensorflowPredictionService = mock(TensorflowPredictionService.class);
    private final PredictionService predictionService
            = new PredictionServiceImpl(
                    0,
            eventService,
            tensorflowPredictionService,
            eventOutcomeService);

    @Test
    void start() throws InterruptedException {
        when(eventService.getEvents("greece_1")).thenReturn(
                Flux.just(Event.builder().build())
        );

        when(eventOutcomeService.save(any())).thenReturn(Mono.just(EventOutcome.builder()
                .competition("greece_1").build()));

        predictionService.start("GREECE");

        Thread.sleep(100);
        verify(tensorflowPredictionService, atLeastOnce()).predict(any());
    }

    @Test
    void reprocess(){
        when(eventOutcomeService.toFix()).thenReturn(
                Flux.just(EventOutcome.builder().competition("greece_1")
                        .eventType(Predictions.PREDICT_RESULTS.name()).build())
        );
        predictionService.reProcess();
        verify(tensorflowPredictionService, atLeastOnce()).predict(any());

    }

}