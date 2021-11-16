package com.timmytime.predictorteamsreactive.service.impl;

import com.timmytime.predictorteamsreactive.enumerator.Training;
import com.timmytime.predictorteamsreactive.model.TrainingHistory;
import com.timmytime.predictorteamsreactive.repo.TrainingHistoryRepo;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TrainingHistoryServiceImplTest {

    TrainingHistoryRepo trainingHistoryRepo = mock(TrainingHistoryRepo.class);

    private final TrainingHistoryServiceImpl trainingHistoryService
            = new TrainingHistoryServiceImpl(trainingHistoryRepo);

    @Test
    public void findById() {
        var id = UUID.randomUUID();

        when(trainingHistoryRepo.findById(id)).thenReturn(
                Optional.of(TrainingHistory.builder().build())
        );

        assertNotNull(trainingHistoryService.find(id));

    }

    @Test
    public void findByTypeAndCountry() {
        var country = "england";
        var type = Training.TRAIN_GOALS;

        when(trainingHistoryRepo.findByTypeAndCountryOrderByDateDesc(type, country)).thenReturn(
                Arrays.asList(TrainingHistory.builder().build())
        );

        assertNotNull(trainingHistoryService.find(type, country));

    }
}