package com.timmytime.predictoreventsreactive.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictoreventsreactive.enumerator.Messages;
import com.timmytime.predictoreventsreactive.facade.WebClientFacade;
import com.timmytime.predictoreventsreactive.request.Message;
import com.timmytime.predictoreventsreactive.service.PredictionService;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageReceivedServiceImplTest {

    private final PredictionService predictionService = mock(PredictionService.class);
    private final WebClientFacade webClientFacade = mock(WebClientFacade.class);

    private final MessageReceivedServiceImpl messageReceivedService
            = new MessageReceivedServiceImpl("dummy", predictionService, webClientFacade);

    @Test
    public void messageStartTest() throws InterruptedException, JsonProcessingException {

        Message message = new Message();

        message.setType(Messages.PADDYPOWER_ODDS);
        message.setCountry("ENGLAND");

        messageReceivedService.receive(
                Mono.just(message)).subscribe();

        Message message2 = new Message();

        message2.setType(Messages.BETWAY_ODDS);
        message2.setCountry("ENGLAND");

        messageReceivedService.receive(
                Mono.just(message2)).subscribe();


        Message message3 = new Message();

        message3.setType(Messages.TRAINING_COMPLETED);
        message3.setCountry("ENGLAND");

        messageReceivedService.receive(
                Mono.just(message3)).subscribe();



        Thread.sleep(1000L);

        verify(predictionService, atLeastOnce()).start(any());
        verify(webClientFacade, atLeastOnce()).sendMessage(any(), any());
    }

    @Test
    public void messageDontStartTest() throws InterruptedException {

        Message message = new Message();

        message.setType(Messages.PADDYPOWER_ODDS);
        message.setCountry("ENGLAND");

        messageReceivedService.receive(
                Mono.just(message)).subscribe();


        Thread.sleep(1000L);

        verify(predictionService, never()).start(any());
    }

}