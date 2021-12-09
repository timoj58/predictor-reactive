package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.model.FantasyOutcome;
import com.timmytime.predictorplayerseventsreactive.service.FantasyOutcomeService;
import com.timmytime.predictorplayerseventsreactive.service.PlayerResponseService;
import com.timmytime.predictorplayerseventsreactive.service.PredictionMonitorService;
import com.timmytime.predictorplayerseventsreactive.service.PredictionResultService;
import org.json.JSONArray;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

class PredictionResultServiceImplTest {

    private final FantasyOutcomeService fantasyOutcomeService = mock(FantasyOutcomeService.class);
    private final PlayerResponseService playerResponseService = mock(PlayerResponseService.class);
    private final PredictionMonitorService predictionMonitorService = mock(PredictionMonitorService.class);
    //test data
    JSONArray testData = new JSONArray("" +
            "[{\"0\":{\"score\":\"10\",\"label\":\"0\"},\"1\":{\"score\":\"10\",\"label\":\"1\"},\"2\":{\"score\":\"10\",\"label\":\"2\"},\"id\":\"cc766bbd-954f-4db1-b9fa-a44d7ea641c9\"}]");

    private final PredictionResultService predictionResultService =
            new PredictionResultServiceImpl(
                    fantasyOutcomeService,
                    playerResponseService,
                    predictionMonitorService
            );

    @Test
    void result() throws InterruptedException {

        when(fantasyOutcomeService.find(any())).thenReturn(
                Mono.just(
                        FantasyOutcome.builder().build()
                )
        );
        when(fantasyOutcomeService.save(any())).thenReturn(
                Mono.just(
                        FantasyOutcome.builder().build()
                )
        );
        predictionResultService.result(testData);

        Thread.sleep(100);

        verify(playerResponseService, atLeastOnce()).addResult(any());


    }
}