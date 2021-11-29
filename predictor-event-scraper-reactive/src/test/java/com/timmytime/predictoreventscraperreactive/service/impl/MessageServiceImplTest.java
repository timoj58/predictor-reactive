package com.timmytime.predictoreventscraperreactive.service.impl;

import com.fasterxml.jackson.databind.node.TextNode;
import com.timmytime.predictoreventscraperreactive.enumerator.Providers;
import com.timmytime.predictoreventscraperreactive.facade.WebClientFacade;
import com.timmytime.predictoreventscraperreactive.model.ScraperModel;
import com.timmytime.predictoreventscraperreactive.service.MessageService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class MessageServiceImplTest {

    private final WebClientFacade webClientFacade = mock(WebClientFacade.class);

    private final MessageService messageService
            = new MessageServiceImpl("eventsData", "events", webClientFacade);

    @Test
    void sendModel() {
        messageService.send(ScraperModel.builder().data(new TextNode("")).build());
        verify(webClientFacade, atLeastOnce()).send(any(), any());
    }

    @Test
    void send() {
        messageService.send(Providers.ESPN_ODDS.name(), "greece_1");
        verify(webClientFacade, atLeastOnce()).send(any(), any());
    }

}