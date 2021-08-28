package com.timmytime.predictoreventscraperreactive.service;

import com.timmytime.predictoreventscraperreactive.configuration.FixturesScraperConfiguration;
import com.timmytime.predictoreventscraperreactive.scraper.CompetitionFixtureScraper;
import com.timmytime.predictoreventscraperreactive.service.impl.EspnService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.mock;

@SpringBootTest
public class EspnServiceTest {

    @Autowired
    private FixturesScraperConfiguration fixturesScraperConfiguration;

    private MessageService messageService = mock(MessageService.class);

    private CompetitionFixtureScraper competitionFixtureScraper = new CompetitionFixtureScraper(messageService);


    @Test
    public void sanity() throws InterruptedException {

        EspnService espnService = new EspnService(
                fixturesScraperConfiguration, competitionFixtureScraper
        );
        espnService.scrape();

        Thread.sleep(6000);
    }

}
