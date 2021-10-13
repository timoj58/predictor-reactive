package com.timmytime.predictorscraperreactive.service.impl;

import com.timmytime.predictorscraperreactive.model.ScraperHistory;
import com.timmytime.predictorscraperreactive.repo.ScraperHistoryRepo;
import com.timmytime.predictorscraperreactive.service.CompetitionScraperService;
import com.timmytime.predictorscraperreactive.service.MessageService;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ScraperServiceImplTest {


    private final ScraperHistoryRepo scraperHistoryRepo
            = mock(ScraperHistoryRepo.class);
    private final CompetitionScraperService competitionScraperService
            = mock(CompetitionScraperService.class);
    private final MessageService messageService = mock(MessageService.class);

    private final ScraperServiceImpl scraperService
            = new ScraperServiceImpl(
            competitionScraperService,
            scraperHistoryRepo);


    @Test
    public void historicTest() throws InterruptedException {
        scraperService.historic(); //need a real competition scraper here.

        Thread.sleep(10000L);
    }

    @Test
    public void scrapeTest() throws InterruptedException {
        ScraperHistory scraperHistory = new ScraperHistory();
        scraperHistory.setDate(LocalDateTime.now().minusDays(1));
        scraperHistory.setDaysScraped(3);

        when(scraperHistoryRepo.findFirstByOrderByDateDesc()).thenReturn(scraperHistory);

        scraperService.scrape();

        Thread.sleep(30000L);


    }

}