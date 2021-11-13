package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayerseventsreactive.model.PlayersTrainingHistory;
import com.timmytime.predictorplayerseventsreactive.service.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;


@Disabled
class TrainingServiceImplTest {

    PlayersTrainingHistoryService playersTrainingHistoryService = mock(PlayersTrainingHistoryService.class);
    TensorflowTrainingService tensorflowTrainingService = mock(TensorflowTrainingService.class);
    PlayerMatchService playerMatchService = mock(PlayerMatchService.class);

    private TrainingServiceImpl trainingService
            = new TrainingServiceImpl(
            0,
            playersTrainingHistoryService,
            tensorflowTrainingService);

    @Test
    public void justTrainTest() {
        PlayersTrainingHistory playersTrainingHistory = new PlayersTrainingHistory(
                FantasyEventTypes.ASSISTS, LocalDateTime.now().minusYears(1), LocalDateTime.now().minusYears(1)
        );
        when(playersTrainingHistoryService.save(any())).thenReturn(Mono.just(playersTrainingHistory));
        when(playersTrainingHistoryService.find(any(FantasyEventTypes.class))).thenReturn(Mono.just(playersTrainingHistory));


        trainingService.train(playersTrainingHistory);

        verify(playerMatchService, never()).create(any(), any());
        verify(tensorflowTrainingService, atLeastOnce()).train(any());

    }

    @Test
    public void loadAndTrainTest() throws InterruptedException {

        PlayersTrainingHistory playersTrainingHistory = new PlayersTrainingHistory(
                FantasyEventTypes.GOALS, LocalDateTime.now().minusYears(1), LocalDateTime.now().minusYears(1)
        );

        when(playersTrainingHistoryService.save(any())).thenReturn(Mono.just(playersTrainingHistory));
        when(playersTrainingHistoryService.find(any(FantasyEventTypes.class))).thenReturn(Mono.just(playersTrainingHistory));

        trainingService.train(playersTrainingHistory);

        Thread.sleep(1000L);

        verify(playerMatchService, atLeastOnce()).create(any(), any());
        verify(tensorflowTrainingService, atLeastOnce()).train(any());

    }

}