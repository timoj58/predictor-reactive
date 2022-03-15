package com.timmytime.predictorteamsreactive.service.impl;

import com.timmytime.predictorteamsreactive.enumerator.CountryCompetitions;
import com.timmytime.predictorteamsreactive.enumerator.Training;
import com.timmytime.predictorteamsreactive.model.TrainingHistory;
import com.timmytime.predictorteamsreactive.repo.TrainingHistoryRepo;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

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

    @Test
    void nextTest(){

        when(trainingHistoryRepo.findByTypeAndCountryOrderByDateDesc(
                Training.TRAIN_GOALS, "england"
        )).thenReturn(List.of(TrainingHistory.builder()
                .toDate(LocalDateTime.now()).build()));

        when(trainingHistoryRepo.save(any())).thenReturn(
                TrainingHistory.builder().build()
        );

        var res = trainingHistoryService.next(
                Training.TRAIN_GOALS, "england", 1
        );

        assertNotNull(res);

        verify(trainingHistoryRepo, atLeastOnce()).save(any());
    }

    @Test
    void initTest(){

        trainingHistoryService.init("01-01-2011", "05-01-2011");

        verify(trainingHistoryRepo, atLeast(
                CountryCompetitions.values().length * 2
        )).save(any());
    }
}