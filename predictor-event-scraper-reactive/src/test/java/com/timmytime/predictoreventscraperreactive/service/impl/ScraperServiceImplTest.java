package com.timmytime.predictoreventscraperreactive.service.impl;

import com.timmytime.predictoreventscraperreactive.service.ScraperService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class ScraperServiceImplTest {

    private final EspnService espnService = mock(EspnService.class);


    private final ScraperService scraperService
            = new ScraperServiceImpl(espnService);

    @Test
    void scrape() {
        scraperService.scrape().subscribe();
        verify(espnService, atLeastOnce()).scrape();
    }

}