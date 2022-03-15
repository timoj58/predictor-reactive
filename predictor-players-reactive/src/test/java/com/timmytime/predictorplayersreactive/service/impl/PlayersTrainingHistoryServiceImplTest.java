package com.timmytime.predictorplayersreactive.service.impl;

import com.timmytime.predictorplayersreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayersreactive.repo.PlayersTrainingHistoryRepo;
import com.timmytime.predictorplayersreactive.service.PlayersTrainingHistoryService;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.mockito.Mockito.*;

class PlayersTrainingHistoryServiceImplTest {

    private final PlayersTrainingHistoryRepo playersTrainingHistoryRepo = mock(PlayersTrainingHistoryRepo.class);

    private final PlayersTrainingHistoryService playersTrainingHistoryService
            = new PlayersTrainingHistoryServiceImpl(playersTrainingHistoryRepo);

    @Test
    void initTest(){

        playersTrainingHistoryService.init("01-01-2011", "06-01-2011");


        verify(playersTrainingHistoryRepo, atLeast(
                (int) Arrays.stream(FantasyEventTypes.values()).filter(
                        f -> f.getPredict() == Boolean.TRUE
                ).count()
        )).save(any());

    }

}