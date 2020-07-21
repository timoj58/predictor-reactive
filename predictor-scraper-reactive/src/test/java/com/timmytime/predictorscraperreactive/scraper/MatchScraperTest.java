package com.timmytime.predictorscraperreactive.scraper;

import com.timmytime.predictorscraperreactive.configuration.SiteRules;
import com.timmytime.predictorscraperreactive.enumerator.ScraperTypeKeys;
import com.timmytime.predictorscraperreactive.factory.SportsScraperConfigurationFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchScraperTest {

    private final SportsScraperConfigurationFactory sportsScraperConfigurationFactory
            = new SportsScraperConfigurationFactory("./src/main/resources/config/");


    private final MatchScraper matchScraper
            = new MatchScraper(sportsScraperConfigurationFactory);

    @Test
    public void matchScrapeTest() throws InterruptedException {

        List<SiteRules> siteRules
                = sportsScraperConfigurationFactory
                .getConfig(ScraperTypeKeys.MATCHES)
                .getSportScrapers()
                .stream()
                .findFirst()
                .get()
                .getSiteRules();

        assertEquals(6,
                matchScraper.scrape(541766)
                .getData().size()
        );

        Thread.sleep(2000L);
    }

}