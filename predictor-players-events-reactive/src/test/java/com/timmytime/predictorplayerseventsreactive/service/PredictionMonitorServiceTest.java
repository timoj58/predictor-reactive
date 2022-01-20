package com.timmytime.predictorplayerseventsreactive.service;

import com.timmytime.predictorplayerseventsreactive.enumerator.ApplicableFantasyLeagues;
import com.timmytime.predictorplayerseventsreactive.facade.WebClientFacade;
import com.timmytime.predictorplayerseventsreactive.request.TensorflowPrediction;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

class PredictionMonitorServiceTest {

    private final WebClientFacade webClientFacade = mock(WebClientFacade.class);
    private final FantasyOutcomeService fantasyOutcomeService = mock(FantasyOutcomeService.class);
    private final TensorflowPredictionService tensorflowPredictionService = mock(TensorflowPredictionService.class);

    private final PredictionMonitorService predictionMonitorService
            = new PredictionMonitorService("message",
            webClientFacade, fantasyOutcomeService, tensorflowPredictionService);


    @Test
    void startTest() throws InterruptedException {

        List.of(
                ApplicableFantasyLeagues.values()
        ).forEach(league -> predictionMonitorService.setStart());

        predictionMonitorService.addPrediction(
                TensorflowPrediction.builder().build()
        );

        Thread.sleep(100L);

        verify(tensorflowPredictionService, atLeastOnce()).predict(any());

    }
}