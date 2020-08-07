package com.timmytime.predictorplayersreactive.service.impl;

import com.timmytime.predictorplayersreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayersreactive.model.LineupPlayer;
import com.timmytime.predictorplayersreactive.model.Player;
import com.timmytime.predictorplayersreactive.model.PlayerMatch;
import com.timmytime.predictorplayersreactive.model.PlayersTrainingHistory;
import com.timmytime.predictorplayersreactive.service.PlayerMatchService;
import com.timmytime.predictorplayersreactive.service.PlayerService;
import com.timmytime.predictorplayersreactive.service.PlayersTrainingHistoryService;
import com.timmytime.predictorplayersreactive.service.TensorflowTrainingService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class TrainingServiceImplTest {

    PlayerService playerService = mock(PlayerService.class);
    PlayersTrainingHistoryService playersTrainingHistoryService = mock(PlayersTrainingHistoryService.class);
    TensorflowTrainingService tensorflowTrainingService = mock(TensorflowTrainingService.class);
    PlayerMatchService playerMatchService = mock(PlayerMatchService.class);

    private TrainingServiceImpl trainingService
            = new TrainingServiceImpl(
                    0,
            0,
            playerService,
            playersTrainingHistoryService,
            tensorflowTrainingService,
            playerMatchService);

    @Test
    public void justTrainTest()  {
        PlayersTrainingHistory playersTrainingHistory = new PlayersTrainingHistory(
                FantasyEventTypes.ASSISTS, LocalDateTime.now().minusYears(1),LocalDateTime.now().minusYears(1)
        );
        when(playersTrainingHistoryService.save(any())).thenReturn(Mono.just(playersTrainingHistory));
        when(playersTrainingHistoryService.find(any(FantasyEventTypes.class))).thenReturn(Mono.just(playersTrainingHistory));


        trainingService.train(playersTrainingHistory);

        verify(playerMatchService, never()).create(any(), any(), any());
        verify(tensorflowTrainingService, atLeastOnce()).train(any());

    }

    @Test
    public void loadAndTrainTest() throws InterruptedException {

        PlayersTrainingHistory playersTrainingHistory = new PlayersTrainingHistory(
                FantasyEventTypes.MINUTES, LocalDateTime.now().minusYears(1),LocalDateTime.now().minusYears(1)
        );

        when(playersTrainingHistoryService.save(any())).thenReturn(Mono.just(playersTrainingHistory));
        when(playersTrainingHistoryService.find(any(FantasyEventTypes.class))).thenReturn(Mono.just(playersTrainingHistory));
        when(playerService.get()).thenReturn(Arrays.asList(new Player()));

        trainingService.train(playersTrainingHistory);

        Thread.sleep(1000L);

        verify(playerService, atLeastOnce()).get();
        verify(playerMatchService, atLeastOnce()).create(any(), any(), any());

        verify(tensorflowTrainingService, atLeastOnce()).train(any());

    }

}