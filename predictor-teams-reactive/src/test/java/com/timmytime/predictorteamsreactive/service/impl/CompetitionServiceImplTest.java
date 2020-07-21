package com.timmytime.predictorteamsreactive.service.impl;

import com.timmytime.predictorteamsreactive.facade.WebClientFacade;
import com.timmytime.predictorteamsreactive.model.Match;
import com.timmytime.predictorteamsreactive.model.TrainingHistory;
import com.timmytime.predictorteamsreactive.service.CompetitionService;
import com.timmytime.predictorteamsreactive.service.TensorflowDataService;
import com.timmytime.predictorteamsreactive.service.TensorflowTrainService;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompetitionServiceImplTest {

    private final TensorflowDataService tensorflowDataService = mock(TensorflowDataService.class);
    private final TensorflowTrainService tensorflowTrainService = mock(TensorflowTrainService.class);
    private final WebClientFacade webClientFacade = mock(WebClientFacade.class);


    private final CompetitionServiceImpl competitionService
            = new CompetitionServiceImpl("dummy", tensorflowTrainService, tensorflowDataService, webClientFacade);

    @Test
    public void trainingTest() throws InterruptedException {

        TrainingHistory trainingHistory = new TrainingHistory();
        trainingHistory.setCountry("england");
        trainingHistory.setToDate(LocalDateTime.now());
        trainingHistory.setFromDate(LocalDateTime.now());

        when(webClientFacade.getMatches(any())).thenReturn(
                Flux.fromStream(Arrays.asList(
                        new Match()
                ).stream())
        );


        competitionService.load(trainingHistory);

        Thread.sleep(1000L);

        verify(tensorflowDataService, atLeastOnce()).load(any());
        verify(tensorflowTrainService, atLeastOnce()).train(any());

    }

}