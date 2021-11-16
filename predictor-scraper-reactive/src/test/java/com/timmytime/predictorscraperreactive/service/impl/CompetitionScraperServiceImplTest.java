package com.timmytime.predictorscraperreactive.service.impl;

import com.timmytime.predictorscraperreactive.configuration.ResultsConfiguration;
import com.timmytime.predictorscraperreactive.enumerator.CompetitionFixtureCodes;
import com.timmytime.predictorscraperreactive.factory.ScraperFactory;
import com.timmytime.predictorscraperreactive.scraper.ResultScraper;
import com.timmytime.predictorscraperreactive.service.PageService;
import com.timmytime.predictorscraperreactive.service.ScraperTrackerService;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.Mockito.*;

class CompetitionScraperServiceImplTest {

    private final ScraperFactory scraperFactory = mock(ScraperFactory.class);
    private final PageService pageService = mock(PageService.class);
    private final ResultsConfiguration resultsConfiguration = mock(ResultsConfiguration.class);

    private final CompetitionScraperServiceImpl competitionScraperService
            = new CompetitionScraperServiceImpl(
            scraperFactory, pageService, resultsConfiguration
    );

    @Test
    void scrape() {
        ResultScraper resultScraper = mock(ResultScraper.class);
        when(scraperFactory.getResultScraper()).thenReturn(resultScraper);
        when(resultsConfiguration.getUrls())
                .thenReturn(Arrays.asList(
                        Pair.of(CompetitionFixtureCodes.ITALY_1, "")
                ));
        competitionScraperService.scrape(LocalDateTime.now());

        verify(resultScraper, atLeastOnce()).createRequest(any(), any());
    }

    @Test
    void setResultsInQueue() {
        ScraperTrackerService scraperTrackerService = mock(ScraperTrackerService.class);
        when(scraperFactory.getScraperTrackerService()).thenReturn(scraperTrackerService);
        competitionScraperService.setResultsInQueue(1);
        verify(scraperTrackerService, atLeastOnce()).addResultsInQueue(CompetitionFixtureCodes.ENGLAND_1, 1);
    }

}