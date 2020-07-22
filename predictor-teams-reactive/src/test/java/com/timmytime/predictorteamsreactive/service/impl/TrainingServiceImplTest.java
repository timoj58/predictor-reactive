package com.timmytime.predictorteamsreactive.service.impl;

import com.timmytime.predictorteamsreactive.facade.WebClientFacade;
import com.timmytime.predictorteamsreactive.model.Match;
import com.timmytime.predictorteamsreactive.model.TrainingHistory;
import com.timmytime.predictorteamsreactive.service.TensorflowDataService;
import com.timmytime.predictorteamsreactive.service.TensorflowTrainService;
import com.timmytime.predictorteamsreactive.service.TrainingHistoryService;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TrainingServiceImplTest {

    private final TensorflowDataService tensorflowDataService = mock(TensorflowDataService.class);
    private final TensorflowTrainService tensorflowTrainService = mock(TensorflowTrainService.class);
    private final TrainingHistoryService trainingHistoryService = mock(TrainingHistoryService.class);
    private final WebClientFacade webClientFacade = mock(WebClientFacade.class);


    private final TrainingServiceImpl trainingService
            = new TrainingServiceImpl("dummy", 0, 0,
            tensorflowDataService, tensorflowTrainService, trainingHistoryService, webClientFacade);

    @Test
    public void trainingTest() throws InterruptedException {

        when(trainingHistoryService.find(anyString()))
                .thenReturn(new TrainingHistory("country", LocalDateTime.now().minusYears(10), LocalDateTime.now().minusYears(10)));

        when(webClientFacade.getMatches(anyString())).thenReturn(Flux.fromStream(
                Arrays.asList(new Match()).stream()
        ));

        when(trainingHistoryService.save(any())).thenReturn(
                new TrainingHistory("test", LocalDateTime.now(), LocalDateTime.now())
        );

        trainingService.train();

        Thread.sleep(1000);


        verify(tensorflowTrainService, atLeastOnce()).train(any());


    }

    @Test
    public void trainingFinishedTest(){

        trainingService.train(new TrainingHistory("test", LocalDateTime.now(), LocalDateTime.now()));

        verify(webClientFacade, never()).getMatches(anyString());

    }

}