package com.timmytime.predictorscraperreactive.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.timmytime.predictorscraperreactive.factory.ScraperFactory;
import com.timmytime.predictorscraperreactive.model.Result;
import com.timmytime.predictorscraperreactive.model.ScraperHistory;
import com.timmytime.predictorscraperreactive.model.ScraperModel;
import com.timmytime.predictorscraperreactive.scraper.ResultScraper;
import com.timmytime.predictorscraperreactive.service.MessageService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CompetitionScraperServiceImplTest {

    private static final ScraperFactory scraperFactory = mock(ScraperFactory.class);
    private final MessageService messageService = mock(MessageService.class);
    private final CompetitionScraperServiceImpl
            competitionScraperService = new CompetitionScraperServiceImpl(
            scraperFactory,
            messageService,
            null,
            null
    );

    @BeforeAll
    public static void setUp() throws JsonProcessingException {

        ResultScraper resultScraper = mock(ResultScraper.class);

        when(resultScraper.scrape(any(), any()))
                .thenReturn(Arrays.asList(new Result()));


        when(scraperFactory.getResultScraper())
                .thenReturn(resultScraper);

    }

    @Test
    public void competitionTest() throws InterruptedException {


        ScraperHistory scraperHistory = new ScraperHistory();
        scraperHistory.setDate(LocalDateTime.now());
        scraperHistory.setDaysScraped(1);


        competitionScraperService.scrape(null);

        Thread.sleep(1000L);

        verify(messageService, atLeastOnce()).send(any(ScraperModel.class));

    }

}