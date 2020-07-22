package com.timmytime.predictorplayersreactive.service.impl;

import com.timmytime.predictorplayersreactive.enumerator.Messages;
import com.timmytime.predictorplayersreactive.model.PlayersTrainingHistory;
import com.timmytime.predictorplayersreactive.request.Message;
import com.timmytime.predictorplayersreactive.service.PlayersTrainingHistoryService;
import com.timmytime.predictorplayersreactive.service.PredictionService;
import com.timmytime.predictorplayersreactive.service.TrainingService;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageReceivedServiceImplTest {

    private final PredictionService predictionService = mock(PredictionService.class);
    private final TrainingService trainingService = mock(TrainingService.class);
    private final PlayersTrainingHistoryService playersTrainingHistoryService = mock(PlayersTrainingHistoryService.class);

    private final MessageReceivedServiceImpl messageReceivedService
            = new MessageReceivedServiceImpl(predictionService, trainingService, playersTrainingHistoryService);

    @Test
    public void messageProcessTest(){

        Message message = new Message();
        message.setCountry("portugal");
        message.setType(Messages.DATA_LOADED);
        messageReceivedService.receive(Mono.just(message)).subscribe();

        Message message2 = new Message();
        message2.setCountry("portugal");
        message2.setType(Messages.EVENTS_LOADED);
        messageReceivedService.receive(Mono.just(message2)).subscribe();

        verify(predictionService, atLeastOnce()).start(any());
    }


    @Test
    public void messageNoProcessTest(){

        Message message = new Message();
        message.setCountry("portugal");
        message.setType(Messages.DATA_LOADED);
        messageReceivedService.receive(Mono.just(message)).subscribe();

        verify(predictionService, never()).start(any());

    }

    @Test
    public void trainingFinishedTest() throws InterruptedException {

        when(playersTrainingHistoryService.find(any())).thenReturn(Mono.just(
                new PlayersTrainingHistory(LocalDateTime.now(), LocalDateTime.now())
        ));

        when(playersTrainingHistoryService.save(any())).thenReturn(Mono.just(
                new PlayersTrainingHistory(LocalDateTime.now(), LocalDateTime.now())
        ));

        messageReceivedService.training(UUID.randomUUID()).subscribe();
        Thread.sleep(1000);

        verify(trainingService, never()).train();
    }

    @Test
    public void trainingNotFinishedTest() throws InterruptedException {

        when(playersTrainingHistoryService.find(any())).thenReturn(Mono.just(
                new PlayersTrainingHistory(LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1))
        ));

        when(playersTrainingHistoryService.save(any())).thenReturn(Mono.just(
                new PlayersTrainingHistory(LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1))
        ));

        messageReceivedService.training(UUID.randomUUID()).subscribe();

        Thread.sleep(1000);

        verify(trainingService, atLeastOnce()).train();
    }

}