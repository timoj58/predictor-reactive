package com.timmytime.predictorscraperreactive.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.timmytime.predictorscraperreactive.facade.WebClientFacade;
import com.timmytime.predictorscraperreactive.model.Result;
import com.timmytime.predictorscraperreactive.request.Message;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class MessageServiceImplTest {

    WebClientFacade webClientFacade = mock(WebClientFacade.class);

    private final MessageServiceImpl messageService
            = new MessageServiceImpl("data", "team", webClientFacade);

    @Test
    void sendMessage() {
        messageService.send(new Message("england_1"));

        verify(webClientFacade, atLeastOnce()).send(anyString(), any(Message.class));
    }

    @Test
    void sendModel() {
        messageService.send(new Result());

        verify(webClientFacade, atLeastOnce()).send(anyString(), any(JsonNode.class));

        assertTrue(messageService.getMessagesSentCount() == 1);
    }

}