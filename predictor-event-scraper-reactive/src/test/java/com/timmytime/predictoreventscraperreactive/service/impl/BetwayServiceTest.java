package com.timmytime.predictoreventscraperreactive.service.impl;

import com.timmytime.predictoreventscraperreactive.factory.BetwayScraperFactory;
import com.timmytime.predictoreventscraperreactive.factory.BookmakerScraperConfigurationFactory;
import com.timmytime.predictoreventscraperreactive.model.ScraperModel;
import com.timmytime.predictoreventscraperreactive.scraper.betway.BetwayEventSpecificScraper;
import com.timmytime.predictoreventscraperreactive.scraper.betway.BetwayEventsScraper;
import com.timmytime.predictoreventscraperreactive.service.MessageService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Disabled
class BetwayServiceTest {

    private final MessageService messageService = mock(MessageService.class);
    private final static BetwayScraperFactory betwayScraperFactory = mock(BetwayScraperFactory.class);

    private final BetwayService betwayService
            = new BetwayService(0,
            new BookmakerScraperConfigurationFactory("./src/main/resources/config/"),
            betwayScraperFactory,
            messageService
            );

    @BeforeAll
    public static void setUp(){

        BetwayEventsScraper betwayEventsScraper = mock(BetwayEventsScraper.class);
        BetwayEventSpecificScraper betwayEventSpecificScraper = mock(BetwayEventSpecificScraper.class);

        when(betwayEventsScraper.scrape(any(), any())).thenReturn(Arrays.asList(1, 2, 3));

        when(betwayScraperFactory.getEventsScraper()).thenReturn(betwayEventsScraper);
        when(betwayScraperFactory.getEventScraper()).thenReturn(betwayEventSpecificScraper);




    }

    @Test
    public void scrapeTest() throws InterruptedException {
        betwayService.scrape();

        Thread.sleep(10000L);

        verify(messageService, atLeast(27)).send(any(), any());
        verify(messageService, atLeastOnce()).send(any());
    }

}