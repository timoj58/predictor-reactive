package com.timmytime.predictoreventsreactive.service.impl;

import com.timmytime.predictoreventsreactive.enumerator.Predictions;
import com.timmytime.predictoreventsreactive.model.Event;
import com.timmytime.predictoreventsreactive.model.EventOutcome;
import com.timmytime.predictoreventsreactive.request.TensorflowPrediction;
import com.timmytime.predictoreventsreactive.service.*;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

class PredictionServiceImplTest {

    private static final EventOutcomeService eventOutcomeService = mock(EventOutcomeService.class);
    private final PredictionMonitorService predictionMonitorService = mock(PredictionMonitorService.class);
    private final EventService eventService = mock(EventService.class);
    private final TensorflowPredictionService tensorflowPredictionService = mock(TensorflowPredictionService.class);
    private final PredictionService predictionService
            = new PredictionServiceImpl(
            eventService,
            predictionMonitorService,
            eventOutcomeService);

    @Test
    void start() throws InterruptedException {
        when(eventService.getEvents("greece_1")).thenReturn(
                Flux.just(Event.builder().competition("greece_1").build())
        );

        when(eventOutcomeService.save(any())).thenReturn(Mono.just(EventOutcome.builder()
                        .eventType(Predictions.PREDICT_GOALS.name())
                .competition("greece_1").build()));

        predictionService.start("GREECE");

        Thread.sleep(100);
        verify(predictionMonitorService, atLeastOnce()).addPrediction(any(TensorflowPrediction.class));
    }

}