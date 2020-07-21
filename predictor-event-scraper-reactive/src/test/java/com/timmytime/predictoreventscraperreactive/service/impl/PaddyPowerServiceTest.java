package com.timmytime.predictoreventscraperreactive.service.impl;

import com.timmytime.predictoreventscraperreactive.facade.ScraperProxyFacade;
import com.timmytime.predictoreventscraperreactive.factory.BookmakerScraperConfigurationFactory;
import com.timmytime.predictoreventscraperreactive.factory.PaddyPowerScraperFactory;
import com.timmytime.predictoreventscraperreactive.scraper.betway.BetwayEventSpecificScraper;
import com.timmytime.predictoreventscraperreactive.scraper.betway.BetwayEventsScraper;
import com.timmytime.predictoreventscraperreactive.scraper.paddypower.PaddyPowerAppKeyScraper;
import com.timmytime.predictoreventscraperreactive.scraper.paddypower.PaddyPowerEventSpecificScraper;
import com.timmytime.predictoreventscraperreactive.scraper.paddypower.PaddyPowerEventsScraper;
import com.timmytime.predictoreventscraperreactive.service.MessageService;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaddyPowerServiceTest {

    private final static PaddyPowerScraperFactory paddyPowerScraperFactory = mock(PaddyPowerScraperFactory.class);
    private final MessageService messageService = mock(MessageService.class);

    private final PaddyPowerService paddyPowerService
            = new PaddyPowerService(
                    0,
            0,
            new BookmakerScraperConfigurationFactory("./src/main/resources/config/"),
            paddyPowerScraperFactory,
            mock(ScraperProxyFacade.class),
            messageService
    );

    @BeforeAll
    public static void setUp(){

        PaddyPowerAppKeyScraper paddyPowerAppKeyScraper = mock(PaddyPowerAppKeyScraper.class);
        PaddyPowerEventsScraper paddyPowerEventsScraper = mock(PaddyPowerEventsScraper.class);
        PaddyPowerEventSpecificScraper paddyPowerEventSpecificScraper = mock(PaddyPowerEventSpecificScraper.class);

        when(paddyPowerAppKeyScraper.scrape(any())).thenReturn("test");
        when(paddyPowerEventsScraper.scrape(any(), any())).thenReturn(new JSONObject());

        when(paddyPowerScraperFactory.getAppKeyScraper(any())).thenReturn(paddyPowerAppKeyScraper);
        when(paddyPowerScraperFactory.getEventsScraper(any())).thenReturn(paddyPowerEventsScraper);
        when(paddyPowerScraperFactory.getEventScraper(any())).thenReturn(paddyPowerEventSpecificScraper);


    }

    @Test
    public void scraperTest() throws InterruptedException {

        paddyPowerService.scrape();

        Thread.sleep(1000L);

        verify(messageService, atLeastOnce()).send(any(), any());
        verify(messageService, atLeastOnce()).send(any());

    }

}