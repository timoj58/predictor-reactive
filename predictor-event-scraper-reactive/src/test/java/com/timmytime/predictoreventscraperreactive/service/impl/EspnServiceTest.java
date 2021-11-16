package com.timmytime.predictoreventscraperreactive.service.impl;

import com.timmytime.predictoreventscraperreactive.configuration.CompetitionFixtures;
import com.timmytime.predictoreventscraperreactive.configuration.FixturesScraperConfiguration;
import com.timmytime.predictoreventscraperreactive.scraper.CompetitionFixtureScraper;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.mockito.Mockito.*;

public class EspnServiceTest {

    private final FixturesScraperConfiguration fixturesScraperConfiguration
            = mock(FixturesScraperConfiguration.class);

    private final CompetitionFixtureScraper competitionFixtureScraper = mock(CompetitionFixtureScraper.class);
    private final EspnService espnService = new EspnService(
            fixturesScraperConfiguration, competitionFixtureScraper
    );

    @Test
    public void scrape() throws InterruptedException {

        when(fixturesScraperConfiguration.getCompetitionFixtures())
                .thenReturn(Arrays.asList(
                        CompetitionFixtures.builder().build()
                ));

        espnService.scrape();
        Thread.sleep(500);
        verify(competitionFixtureScraper, atLeastOnce()).scrape(any());
    }

}
