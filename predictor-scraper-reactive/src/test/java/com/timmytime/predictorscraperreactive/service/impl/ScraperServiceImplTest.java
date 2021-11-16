package com.timmytime.predictorscraperreactive.service.impl;

import com.timmytime.predictorscraperreactive.model.ScraperHistory;
import com.timmytime.predictorscraperreactive.repo.ScraperHistoryRepo;
import com.timmytime.predictorscraperreactive.service.CompetitionScraperService;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

class ScraperServiceImplTest {

    private final CompetitionScraperService competitionScraperService = mock(CompetitionScraperService.class);
    private final ScraperHistoryRepo scraperHistoryRepo = mock(ScraperHistoryRepo.class);

    private final ScraperServiceImpl scraperService
            = new ScraperServiceImpl(competitionScraperService, scraperHistoryRepo);

    @Test
    void scrape() {

        when(scraperHistoryRepo.findFirstByOrderByDateDesc())
                .thenReturn(ScraperHistory.builder()
                        .date(LocalDateTime.now().minusDays(1)).build());

        scraperService.scrape().subscribe();
        verify(competitionScraperService, atLeastOnce()).scrape(any());
    }

    @Test
    void historic() throws InterruptedException {

        scraperService.historic().subscribe();

        Thread.sleep(250);
        verify(competitionScraperService, atLeastOnce()).scrape(any());
    }

}