package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayerseventsreactive.facade.WebClientFacade;
import com.timmytime.predictorplayerseventsreactive.model.Player;
import com.timmytime.predictorplayerseventsreactive.model.PlayersTrainingHistory;
import com.timmytime.predictorplayerseventsreactive.service.*;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

class TrainingModelServiceImplTest {

    private final WebClientFacade webClientFacade = mock(WebClientFacade.class);
    private final PlayersTrainingHistoryService playersTrainingHistoryService = mock(PlayersTrainingHistoryService.class);
    private final PlayerMatchService playerMatchService = mock(PlayerMatchService.class);
    private final TrainingService trainingService = mock(TrainingService.class);
    private final TensorflowDataService tensorflowDataService = mock(TensorflowDataService.class);

    private final TrainingModelService trainingModelService
            = new TrainingModelServiceImpl("data", true,
            webClientFacade, playersTrainingHistoryService, playerMatchService, trainingService, tensorflowDataService);

    @Test
    void create() throws InterruptedException {
        when(playersTrainingHistoryService.find(any(FantasyEventTypes.class))).thenReturn(
                Mono.just(PlayersTrainingHistory.builder().build())
        );

        when(webClientFacade.getPlayers(anyString()))
                .thenReturn(Flux.just(Player.builder()
                        .lastAppearance(LocalDate.now()).build()));

        when(trainingService.firstTrainingEvent()).thenReturn(FantasyEventTypes.GOALS);

        trainingModelService.create();

        Thread.sleep(4000);

        verify(trainingService, atLeastOnce()).train(any(PlayersTrainingHistory.class));

    }

    @Test
    void next() throws InterruptedException {
        when(playersTrainingHistoryService.find(any(FantasyEventTypes.class))).thenReturn(
                Mono.just(PlayersTrainingHistory.builder().build())
        );

        when(webClientFacade.getPlayers(anyString()))
                .thenReturn(Flux.just(Player.builder()
                        .lastAppearance(LocalDate.now()).build()));

        when(trainingService.firstTrainingEvent()).thenReturn(FantasyEventTypes.GOALS);

        trainingModelService.next(PlayersTrainingHistory.builder().build());

        Thread.sleep(600);

        verify(trainingService, atLeastOnce()).train(any(PlayersTrainingHistory.class));

    }
}