package com.timmytime.predictorplayersreactive.service.impl;

import com.timmytime.predictorplayersreactive.model.Event;
import com.timmytime.predictorplayersreactive.model.FantasyOutcome;
import com.timmytime.predictorplayersreactive.model.Player;
import com.timmytime.predictorplayersreactive.service.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PredictionServiceImplTest {


    private static final FantasyOutcomeService fantasyEventOutcomeService = mock(FantasyOutcomeService.class);
    private static final EventsService eventsService = mock(EventsService.class);
    private static final PlayerService playerService = mock(PlayerService.class);
    private static final PlayerResponseService playerResponseService = mock(PlayerResponseService.class);
    private static final TensorflowPredictionService tensorflowPredictionService = mock(TensorflowPredictionService.class);

    private final PredictionServiceImpl predictionService
            = new PredictionServiceImpl(
                    eventsService, playerService, playerResponseService, tensorflowPredictionService, fantasyEventOutcomeService
    );

    @BeforeAll
    public static void setUp(){
        when(eventsService.get(any())).thenReturn(Flux.fromStream(Arrays.asList(new Event()).stream()));
        when(playerService.get(any(), any())).thenReturn(Arrays.asList(new Player()));

        when(fantasyEventOutcomeService.save(any())).thenReturn(Mono.just(new FantasyOutcome()));

    }

    @Test
    public void startTest() throws InterruptedException {

        predictionService.start("PORTUGAL");

        Thread.sleep(2000L);

        verify(eventsService, atLeastOnce()).get(any());
        verify(playerService, atLeastOnce()).get(any(), any());
        verify(tensorflowPredictionService, atLeastOnce()).predict(any(), any());


    }

}