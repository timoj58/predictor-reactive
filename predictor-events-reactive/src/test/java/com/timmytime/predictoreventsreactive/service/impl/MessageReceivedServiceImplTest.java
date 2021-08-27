package com.timmytime.predictoreventsreactive.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.timmytime.predictoreventsreactive.enumerator.Messages;
import com.timmytime.predictoreventsreactive.facade.WebClientFacade;
import com.timmytime.predictoreventsreactive.request.Message;
import com.timmytime.predictoreventsreactive.service.PredictionMonitorService;
import com.timmytime.predictoreventsreactive.service.PredictionResultService;
import com.timmytime.predictoreventsreactive.service.PredictionService;
import com.timmytime.predictoreventsreactive.service.ValidationService;
import lombok.Getter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

class MessageReceivedServiceImplTest {

    private final PredictionService predictionService = mock(PredictionService.class);
    private final PredictionResultService predictionResultService = mock(PredictionResultService.class);
    private final WebClientFacade webClientFacade = mock(WebClientFacade.class);
    private final ValidationService validationService = mock(ValidationService.class);

    private final MessageReceivedServiceImpl messageReceivedService
            = new MessageReceivedServiceImpl("dummy", predictionService, predictionResultService, mock(PredictionMonitorService.class), validationService, webClientFacade);


    @Test
    @Disabled
    public void messageStartTest() throws InterruptedException, JsonProcessingException {

        Message message = new Message();

        message.setType(Messages.ESPN_ODDS);
        message.setCountry("ENGLAND");

        messageReceivedService.receive(
                Mono.just(message)).subscribe();

        Message message2 = new Message();

        message2.setType(Messages.ESPN_ODDS);
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

        message.setType(Messages.ESPN_ODDS);
        message.setCountry("ENGLAND");

        messageReceivedService.receive(
                Mono.just(message)).subscribe();


        Thread.sleep(1000L);

        verify(predictionService, never()).start(any());
    }

}