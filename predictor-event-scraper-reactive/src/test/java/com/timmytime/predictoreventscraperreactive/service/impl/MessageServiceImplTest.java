package com.timmytime.predictoreventscraperreactive.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictoreventscraperreactive.enumerator.ScraperTypeKeys;
import com.timmytime.predictoreventscraperreactive.facade.WebClientFacade;
import com.timmytime.predictoreventscraperreactive.model.ScraperModel;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class MessageServiceImplTest {

    private final WebClientFacade webClientFacade = mock(WebClientFacade.class);
    private final MessageServiceImpl messageService
            = new MessageServiceImpl("dummy", "dummy", webClientFacade);

    @Test
    public void dontSendMessageTest(){

        messageService.send(ScraperTypeKeys.PADDYPOWER_ODDS.name(), "england_1");

        verify(webClientFacade, never()).send(any(), any());

    }

    @Test
    public void sendMessageTest(){

        messageService.send(ScraperTypeKeys.PADDYPOWER_ODDS.name(), "italy_1");
        messageService.send(ScraperTypeKeys.PADDYPOWER_ODDS.name(), "italy_2");

        verify(webClientFacade, atLeastOnce()).send(any(), any());

    }

    @Test
    public void dontSendMessageTest2(){

        messageService.send(ScraperTypeKeys.BETWAY_ODDS.name(), "england_1");

        verify(webClientFacade, never()).send(any(), any());

    }

    @Test
    public void sendMessageTest2(){

        messageService.send(ScraperTypeKeys.BETWAY_ODDS.name(), "italy_1");
        messageService.send(ScraperTypeKeys.BETWAY_ODDS.name(), "italy_2");

        verify(webClientFacade, atLeastOnce()).send(any(), any());

    }

    @Test
    public void sendModelTest() throws JsonProcessingException {

        ScraperModel scraperModel = new ScraperModel();
        scraperModel.setData(new ObjectMapper().readTree("{}"));

        messageService.send(scraperModel);

        verify(webClientFacade, atLeastOnce()).send(any(), any());

    }

    @Test
    public void dontSendModelTest() throws JsonProcessingException {

        ScraperModel scraperModel = new ScraperModel();

        messageService.send(scraperModel);

        verify(webClientFacade, never()).send(any(), any());

    }


}