package com.timmytime.predictorscraperreactive.service.impl;

import com.timmytime.predictorscraperreactive.factory.SportsScraperConfigurationFactory;
import com.timmytime.predictorscraperreactive.model.ScraperHistory;
import com.timmytime.predictorscraperreactive.repo.ScraperHistoryRepo;
import com.timmytime.predictorscraperreactive.service.CompetitionScraperService;
import com.timmytime.predictorscraperreactive.service.MessageService;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

class ScraperServiceImplTest {


    private final SportsScraperConfigurationFactory sportsScraperConfigurationFactory
            = new SportsScraperConfigurationFactory("./src/main/resources/config/");
    private final ScraperHistoryRepo scraperHistoryRepo
            = mock(ScraperHistoryRepo.class);
    private final CompetitionScraperService competitionScraperService
            = mock(CompetitionScraperService.class);
    private final MessageService messageService = mock(MessageService.class);

    private final ScraperServiceImpl scraperService
            = new ScraperServiceImpl(
            0,
            sportsScraperConfigurationFactory,
            competitionScraperService,
            scraperHistoryRepo,
            messageService);


    @Test
    public void scrapeTest() throws InterruptedException {
        ScraperHistory scraperHistory = new ScraperHistory();
        scraperHistory.setDate(LocalDateTime.now().minusDays(1));
        scraperHistory.setDaysScraped(3);

        when(scraperHistoryRepo.findFirstByOrderByDateDesc()).thenReturn(scraperHistory);

        scraperService.scrape();

        Thread.sleep(30000L);

        verify(competitionScraperService, atLeast(14)).scrape(
                any(), any());

        verify(messageService, atLeastOnce()).send();

    }

}