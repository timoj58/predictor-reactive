package com.timmytime.predictorscraperreactive.service.impl;

import com.timmytime.predictorscraperreactive.configuration.SiteRules;
import com.timmytime.predictorscraperreactive.enumerator.ScraperTypeKeys;
import com.timmytime.predictorscraperreactive.factory.SportsScraperConfigurationFactory;
import com.timmytime.predictorscraperreactive.model.ScraperHistory;
import com.timmytime.predictorscraperreactive.repo.ScraperHistoryRepo;
import com.timmytime.predictorscraperreactive.service.CompetitionScraperService;
import com.timmytime.predictorscraperreactive.service.MessageService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Disabled
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
                    "2020-07-07",
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

        Thread.sleep(20000L);

        verify(competitionScraperService, atLeastOnce()).scrape(
                any(), any());

        verify(messageService, atLeastOnce()).send();

    }

}