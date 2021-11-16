package com.timmytime.predictorteamsreactive.service.impl;

import com.timmytime.predictorteamsreactive.enumerator.Training;
import com.timmytime.predictorteamsreactive.facade.WebClientFacade;
import com.timmytime.predictorteamsreactive.model.TrainingHistory;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;

class TensorflowTrainServiceImplTest {

    WebClientFacade webClientFacade = mock(WebClientFacade.class);

    private final TensorflowTrainServiceImpl tensorflowTrainService
            = new TensorflowTrainServiceImpl("training", "result", "goals",
            webClientFacade);

    @Test
    public void train() {
        tensorflowTrainService.train(TrainingHistory.builder()
                .country("country")
                .toDate(LocalDateTime.now())
                .fromDate(LocalDateTime.now())
                .id(UUID.randomUUID())
                .type(Training.TRAIN_GOALS).build());

        verify(webClientFacade, atLeastOnce()).train(anyString());
    }
}