package com.timmytime.predictorscraperreactive.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.timmytime.predictorscraperreactive.configuration.SiteRules;
import com.timmytime.predictorscraperreactive.enumerator.ScraperTypeKeys;
import com.timmytime.predictorscraperreactive.factory.ScraperFactory;
import com.timmytime.predictorscraperreactive.factory.SportsScraperConfigurationFactory;
import com.timmytime.predictorscraperreactive.model.*;
import com.timmytime.predictorscraperreactive.scraper.LineupScraper;
import com.timmytime.predictorscraperreactive.scraper.MatchScraper;
import com.timmytime.predictorscraperreactive.scraper.ResultScraper;
import com.timmytime.predictorscraperreactive.service.MessageService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CompetitionScraperServiceImplTest {

    private final SportsScraperConfigurationFactory sportsScraperConfigurationFactory
            = new SportsScraperConfigurationFactory("./src/main/resources/config/");

    private final MessageService messageService = mock(MessageService.class);
    private static final ScraperFactory scraperFactory = mock(ScraperFactory.class);



    private final CompetitionScraperServiceImpl
    competitionScraperService = new CompetitionScraperServiceImpl(
            0,
            0,
            sportsScraperConfigurationFactory,
            scraperFactory,
            messageService
    );

    @BeforeAll
    public static void setUp() throws JsonProcessingException {

        LineupScraper lineupScraper = mock(LineupScraper.class);
        MatchScraper matchScraper = mock(MatchScraper.class);
        ResultScraper resultScraper = mock(ResultScraper.class);

        when(resultScraper.scrape(any(), any()))
                .thenReturn(Arrays.asList(new Result()));

        when(matchScraper.scrape(any()))
                .thenReturn(new Match());

        when(lineupScraper.scrape(any()))
                .thenReturn(new Lineup());

        when(scraperFactory.getLineupScraper(any()))
                .thenReturn(lineupScraper);

        when(scraperFactory.getMatchScraper(any()))
                .thenReturn(matchScraper);

        when(scraperFactory.getResultScraper(any()))
                .thenReturn(resultScraper);


    }

    @Test
    public void competitionTest() throws InterruptedException {

        SiteRules england1 =
                sportsScraperConfigurationFactory.getConfig(ScraperTypeKeys.RESULTS)
                        .getSportScrapers()
                        .stream()
                        .findFirst()
                        .get()
                        .getSiteRules().stream().filter(f -> f.getId().equals("england_1"))
                        .findFirst()
                        .get();

        ScraperHistory scraperHistory = new ScraperHistory();
        scraperHistory.setDate(LocalDateTime.now());
        scraperHistory.setDaysScraped(1);


        competitionScraperService.scrape(scraperHistory, england1);

        Thread.sleep(1000L);

        verify(messageService, atLeastOnce()).send(any(ScraperModel.class));

    }

}