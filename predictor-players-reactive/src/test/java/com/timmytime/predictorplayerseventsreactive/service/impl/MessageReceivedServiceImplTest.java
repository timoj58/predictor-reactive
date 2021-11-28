package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.enumerator.CountryCompetitions;
import com.timmytime.predictorplayerseventsreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayerseventsreactive.model.PlayersTrainingHistory;
import com.timmytime.predictorplayerseventsreactive.request.Message;
import com.timmytime.predictorplayerseventsreactive.service.MessageReceivedService;
import com.timmytime.predictorplayerseventsreactive.service.PlayersTrainingHistoryService;
import com.timmytime.predictorplayerseventsreactive.service.TrainingModelService;
import com.timmytime.predictorplayerseventsreactive.service.TrainingService;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;


class MessageReceivedServiceImplTest {

    private final TrainingService trainingService = mock(TrainingService.class);
    private final TrainingModelService trainingModelService = mock(TrainingModelService.class);
    private final PlayersTrainingHistoryService playersTrainingHistoryService = mock(PlayersTrainingHistoryService.class);

    private final MessageReceivedService messageReceivedService
            = new MessageReceivedServiceImpl(
            trainingService, trainingModelService, playersTrainingHistoryService
    );

    @Test
    void receive() {

        when(playersTrainingHistoryService.find(any(FantasyEventTypes.class)))
                .thenReturn(Mono.just(
                        PlayersTrainingHistory.builder().build()
                ));

        when(trainingService.firstTrainingEvent()).thenReturn(FantasyEventTypes.GOALS);

        Stream.of(CountryCompetitions.values())
                .forEach(c ->
                        c.getCompetitions().forEach(comp ->

                                messageReceivedService.receive(Mono.just(
                                        Message.builder()
                                                .eventType("ALL")
                                                .build()
                                )).subscribe()));

        verify(trainingModelService, atLeastOnce()).next(any());
    }

    @Test
    void training() {
        var id = UUID.randomUUID();
        when(playersTrainingHistoryService.find(id)).thenReturn(
                Mono.just(PlayersTrainingHistory.builder().build())
        );

        messageReceivedService.training(id).subscribe();

        verify(trainingService, atLeastOnce()).train(any(PlayersTrainingHistory.class));
    }

    @Test
    void createTrainingModel() throws InterruptedException {
        messageReceivedService.createTrainingModel().subscribe();
        Thread.sleep(100);
        verify(trainingModelService, atLeastOnce()).create();
    }

}