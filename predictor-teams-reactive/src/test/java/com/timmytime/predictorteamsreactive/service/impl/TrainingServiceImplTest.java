package com.timmytime.predictorteamsreactive.service.impl;

import com.timmytime.predictorteamsreactive.enumerator.Training;
import com.timmytime.predictorteamsreactive.facade.WebClientFacade;
import com.timmytime.predictorteamsreactive.model.Match;
import com.timmytime.predictorteamsreactive.model.TrainingHistory;
import com.timmytime.predictorteamsreactive.service.TensorflowDataService;
import com.timmytime.predictorteamsreactive.service.TensorflowTrainService;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

class TrainingServiceImplTest {

    TensorflowDataService tensorflowDataService = mock(TensorflowDataService.class);
    TensorflowTrainService tensorflowTrainService = mock(TensorflowTrainService.class);
    WebClientFacade webClientFacade = mock(WebClientFacade.class);

    private final TrainingServiceImpl trainingService
            = new TrainingServiceImpl("data", 0, false,
            tensorflowDataService, tensorflowTrainService, webClientFacade);

    @Test
    public void trainResults() throws InterruptedException {

        when(webClientFacade.getMatches(anyString()))
                .thenReturn(Flux.just(Match.builder().build()));

        trainingService.train((i) -> TrainingHistory.builder()
                .type(Training.TRAIN_RESULTS)
                .toDate(LocalDateTime.now())
                .fromDate(LocalDateTime.now()).build());

        Thread.sleep(250);

        verify(tensorflowDataService, atLeastOnce()).load(any());
        verify(tensorflowTrainService, atLeastOnce()).train(any());
    }

    @Test
    public void trainGoals() {
        trainingService.train((i) -> TrainingHistory.builder()
                .type(Training.TRAIN_GOALS).build());

        verify(webClientFacade, never()).getMatches(anyString());
        verify(tensorflowTrainService, atLeastOnce()).train(any());
    }
}