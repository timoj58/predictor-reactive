package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayerseventsreactive.model.PlayersTrainingHistory;
import com.timmytime.predictorplayerseventsreactive.service.PlayerMatchService;
import com.timmytime.predictorplayerseventsreactive.service.PlayersTrainingHistoryService;
import com.timmytime.predictorplayerseventsreactive.service.TensorflowTrainingService;
import com.timmytime.predictorplayerseventsreactive.service.TrainingService;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;


class TrainingServiceImplTest {

    private final  PlayersTrainingHistoryService playersTrainingHistoryService = mock(PlayersTrainingHistoryService.class);
    private final TensorflowTrainingService tensorflowTrainingService = mock(TensorflowTrainingService.class);
    private final TrainingService trainingService
            = new TrainingServiceImpl(
            0,
            playersTrainingHistoryService,
            tensorflowTrainingService);

    @Test
    void trainEvent(){
        var history =  PlayersTrainingHistory.builder()
                .fromDate(LocalDateTime.now())
                .toDate(LocalDateTime.now()).build();
        when(playersTrainingHistoryService.find(any(FantasyEventTypes.class)))
                .thenReturn(Mono.just(history));
        when(playersTrainingHistoryService.save(any(PlayersTrainingHistory.class)))
                .thenReturn(Mono.just(history));
        trainingService.train(FantasyEventTypes.GOALS);

        verify(tensorflowTrainingService, atLeastOnce()).train(any());
    }

    @Test
    void trainHistory(){
        var history =  PlayersTrainingHistory.builder()
                .type(FantasyEventTypes.GOALS)
                .fromDate(LocalDateTime.now())
                .toDate(LocalDateTime.now()).build();

        when(playersTrainingHistoryService.find(any(FantasyEventTypes.class)))
                .thenReturn(Mono.just(history));
        when(playersTrainingHistoryService.save(any(PlayersTrainingHistory.class)))
                .thenReturn(Mono.just(history));

        trainingService.train(history);

        verify(tensorflowTrainingService, atLeastOnce()).train(any());
    }

    @Test
    void trainHistoryStop(){
        var history =  PlayersTrainingHistory.builder()
                .type(FantasyEventTypes.GOALS)
                .fromDate(LocalDateTime.now().plusDays(1))
                .toDate(LocalDateTime.now().plusDays(1)).build();

        when(playersTrainingHistoryService.find(any(FantasyEventTypes.class)))
                .thenReturn(Mono.just(history));
        when(playersTrainingHistoryService.save(any(PlayersTrainingHistory.class)))
                .thenReturn(Mono.just(history));

        trainingService.train(history);
        history =  PlayersTrainingHistory.builder()
                .type(FantasyEventTypes.ASSISTS)
                .fromDate(LocalDateTime.now().plusDays(1))
                .toDate(LocalDateTime.now().plusDays(1)).build();
        trainingService.train(history);

        history =  PlayersTrainingHistory.builder()
                .type(FantasyEventTypes.YELLOW_CARD)
                .fromDate(LocalDateTime.now().plusDays(1))
                .toDate(LocalDateTime.now().plusDays(1)).build();
        trainingService.train(history);


        verify(tensorflowTrainingService, atMost(2)).train(any());
    }

}