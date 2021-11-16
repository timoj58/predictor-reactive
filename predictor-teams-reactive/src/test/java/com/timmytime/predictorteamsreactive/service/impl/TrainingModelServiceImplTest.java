package com.timmytime.predictorteamsreactive.service.impl;

import com.timmytime.predictorteamsreactive.enumerator.Training;
import com.timmytime.predictorteamsreactive.facade.WebClientFacade;
import com.timmytime.predictorteamsreactive.model.Match;
import com.timmytime.predictorteamsreactive.model.TrainingHistory;
import com.timmytime.predictorteamsreactive.service.TensorflowDataService;
import com.timmytime.predictorteamsreactive.service.TensorflowTrainService;
import com.timmytime.predictorteamsreactive.service.TrainingHistoryService;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TrainingModelServiceImplTest {

    TensorflowDataService tensorflowDataService = mock(TensorflowDataService.class);
    TensorflowTrainService tensorflowTrainService = mock(TensorflowTrainService.class);
    TrainingHistoryService trainingHistoryService = mock(TrainingHistoryService.class);
    WebClientFacade webClientFacade = mock(WebClientFacade.class);

    private final TrainingModelServiceImpl trainingModelService
            = new TrainingModelServiceImpl("data", 0,
            tensorflowDataService, tensorflowTrainService, trainingHistoryService, webClientFacade
    );

    @Test
    public void create() {

        when(trainingHistoryService.next(any(Training.class), anyString(), anyInt()))
                .thenReturn(TrainingHistory.builder()
                        .country("england")
                        .fromDate(LocalDateTime.now())
                        .toDate(LocalDateTime.now()).build());

        when(webClientFacade.getMatches(anyString()))
                .thenReturn(Flux.just(Match.builder().build()));

        trainingModelService.create();

        verify(tensorflowDataService, atLeastOnce()).load(any());
        verify(tensorflowTrainService, atLeastOnce()).train(any());
    }
}