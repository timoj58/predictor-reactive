package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.enumerator.Messages;
import com.timmytime.predictorplayerseventsreactive.request.Message;
import com.timmytime.predictorplayerseventsreactive.service.PredictionMonitorService;
import com.timmytime.predictorplayerseventsreactive.service.PredictionResultService;
import com.timmytime.predictorplayerseventsreactive.service.PredictionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

class MessageReceivedServiceImplTest {

    @Mock
    private PredictionService predictionService;
    @Mock
    private PredictionResultService predictionResultService;

    @Mock
    private PredictionMonitorService predictionMonitorService;

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
        message.setType(Messages.DATA_LOADED);
        messageReceivedService.receive(Mono.just(message)).subscribe();

        Message message2 = new Message();
        message2.setCountry("portugal");
        message2.setType(Messages.EVENTS_LOADED);
        messageReceivedService.receive(Mono.just(message2)).subscribe();

        verify(predictionService, atLeastOnce()).start(any());
    }


    @Test
    public void messageNoProcessTest() {

        Message message = new Message();
        message.setCountry("portugal");
        message.setType(Messages.DATA_LOADED);
        messageReceivedService.receive(Mono.just(message)).subscribe();

        verify(predictionService, never()).start(any());

    }

}