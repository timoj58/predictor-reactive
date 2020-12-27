package com.timmytime.predictoreventscraperreactive.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

class ScraperServiceImplTest {

    @Mock
    private BetwayService betwayService;
    @Mock
    private PaddyPowerService paddyPowerService;

    @InjectMocks
    private ScraperServiceImpl scraperService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void initTest() throws InterruptedException {

        scraperService.scrape();

        verify(betwayService, atLeastOnce()).scrape();
        verify(paddyPowerService, atLeastOnce()).scrape();

    }

}