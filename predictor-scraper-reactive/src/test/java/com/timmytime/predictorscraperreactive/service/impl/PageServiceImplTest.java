package com.timmytime.predictorscraperreactive.service.impl;

import com.timmytime.predictorscraperreactive.enumerator.CompetitionFixtureCodes;
import com.timmytime.predictorscraperreactive.enumerator.ScraperType;
import com.timmytime.predictorscraperreactive.factory.ScraperFactory;
import com.timmytime.predictorscraperreactive.scraper.PlayerScraper;
import com.timmytime.predictorscraperreactive.scraper.ResultScraper;
import com.timmytime.predictorscraperreactive.service.MessageService;
import com.timmytime.predictorscraperreactive.service.ScraperTrackerService;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class PageServiceImplTest {
    private final ScraperFactory scraperFactory = mock(ScraperFactory.class);
    private final MessageService messageService = mock(MessageService.class);


    private final PageServiceImpl pageService
            = new PageServiceImpl(scraperFactory, messageService);

    @Test
    void addMatchRequestBadRequest() {

        ScraperTrackerService scraperTrackerService = mock(ScraperTrackerService.class);
        when(scraperFactory.getScraperTrackerService()).thenReturn(scraperTrackerService);


        pageService.addPageRequest(
                Triple.of(CompetitionFixtureCodes.BELGIUM_1, ScraperType.MATCH, "http://random")
        );

        verify(scraperTrackerService, atLeastOnce()).incrementRequest();
        verify(scraperTrackerService, atLeastOnce()).addFailedPlayersRequest(any());
    }

    @Test
    void addResultRequestBadRequest() {

        ScraperTrackerService scraperTrackerService = mock(ScraperTrackerService.class);
        when(scraperFactory.getScraperTrackerService()).thenReturn(scraperTrackerService);


        pageService.addPageRequest(
                Triple.of(CompetitionFixtureCodes.BELGIUM_1, ScraperType.RESULTS, "http://random")
        );

        verify(scraperTrackerService, atLeastOnce()).incrementRequest();
        verify(scraperTrackerService, atLeastOnce()).addFailedResultsRequest(any());
    }


    @Test
    void addPageRequestForResult() {

        var url = "https://www.espn.co.uk/soccer/scoreboard/_/league/eng.2/date/2010-02-16";

        ScraperTrackerService scraperTrackerService = mock(ScraperTrackerService.class);
        when(scraperFactory.getScraperTrackerService()).thenReturn(scraperTrackerService);
        when(scraperFactory.getResultScraper()).thenReturn(new ResultScraper());


        pageService.addPageRequest(
                Triple.of(CompetitionFixtureCodes.ENGLAND_1, ScraperType.RESULTS, url)
        );

        verify(scraperTrackerService, atLeastOnce()).incrementRequest();
        verify(scraperTrackerService, atLeastOnce()).removeResultsFromQueue(any());

        verify(scraperTrackerService, never()).addFailedResultsRequest(any());
    }

    @Test
    void addPageRequestForMatch() throws InterruptedException {

        var url = "https://www.espn.co.uk/football/match/_/gameId/278345";

        ScraperTrackerService scraperTrackerService = mock(ScraperTrackerService.class);
        when(scraperFactory.getScraperTrackerService()).thenReturn(scraperTrackerService);
        when(scraperFactory.getPlayerScraper()).thenReturn(new PlayerScraper(""));


        pageService.addPageRequest(
                Triple.of(CompetitionFixtureCodes.ITALY_1, ScraperType.MATCH, url)
        );

        Thread.sleep(250);

        verify(scraperTrackerService, atLeastOnce()).incrementRequest();
        verify(scraperTrackerService, atLeastOnce()).removeMatch(any());

        verify(scraperTrackerService, never()).addFailedPlayersRequest(any());
    }

}