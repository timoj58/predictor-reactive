package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.request.Message;
import com.timmytime.predictorplayerseventsreactive.service.PlayersTrainingHistoryService;
import com.timmytime.predictorplayerseventsreactive.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

@Disabled
class MessageReceivedServiceImplTest {

    @Mock
    private TrainingService trainingService;

    @Mock
    private PlayersTrainingHistoryService playersTrainingHistoryService;

    @InjectMocks
    private MessageReceivedServiceImpl messageReceivedService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void messageProcessTest() {

        Message message = new Message();
        message.setCountry("portugal");
        messageReceivedService.receive(Mono.just(message)).subscribe();

        Message message2 = new Message();
        message2.setCountry("portugal");
        messageReceivedService.receive(Mono.just(message2)).subscribe();

        //    verify(predictionService, atLeastOnce()).start(any());
    }


    @Test
    public void messageNoProcessTest() {

        Message message = new Message();
        message.setCountry("portugal");
        messageReceivedService.receive(Mono.just(message)).subscribe();

        //     verify(predictionService, never()).start(any());

    }

}