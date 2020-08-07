package com.timmytime.predictorplayersreactive.service.impl;

import com.timmytime.predictorplayersreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayersreactive.enumerator.Messages;
import com.timmytime.predictorplayersreactive.model.PlayersTrainingHistory;
import com.timmytime.predictorplayersreactive.request.Message;
import com.timmytime.predictorplayersreactive.service.PlayersTrainingHistoryService;
import com.timmytime.predictorplayersreactive.service.PredictionService;
import com.timmytime.predictorplayersreactive.service.TrainingService;
import org.junit.jupiter.api.Disabled;
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

}