package com.timmytime.predictoreventsreactive.service.impl;

import com.timmytime.predictoreventsreactive.model.Event;
import com.timmytime.predictoreventsreactive.model.EventOutcome;
import com.timmytime.predictoreventsreactive.service.EventOutcomeService;
import com.timmytime.predictoreventsreactive.service.EventService;
import com.timmytime.predictoreventsreactive.service.TensorflowPredictionService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PredictionServiceImplTest {

    private final EventService eventService = mock(EventService.class);
    private final TensorflowPredictionService tensorflowPredictionService = mock(TensorflowPredictionService.class);
    private final EventOutcomeService eventOutcomeService = mock(EventOutcomeService.class);

    private final PredictionServiceImpl predictionService
            = new PredictionServiceImpl(
                    0,
                    eventService,
            tensorflowPredictionService,
            eventOutcomeService
    );


    @Test
    public void startTest() throws InterruptedException {

        when(eventService.getEvents(any())).thenReturn(
                Flux.fromStream(Arrays.asList(new Event()).stream())
        );
        when(eventOutcomeService.save(any())).thenReturn(Mono.just(EventOutcome.builder().competition("turkey_1").build()));

        predictionService.start("TURKEY");
        Thread.sleep(2000);

        verify(tensorflowPredictionService, atLeastOnce()).predict(any(), any(), any());
    }

}