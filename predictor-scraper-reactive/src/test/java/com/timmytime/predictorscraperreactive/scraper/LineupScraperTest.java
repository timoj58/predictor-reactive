package com.timmytime.predictorscraperreactive.scraper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.timmytime.predictorscraperreactive.configuration.SiteRules;
import com.timmytime.predictorscraperreactive.enumerator.ScraperTypeKeys;
import com.timmytime.predictorscraperreactive.factory.SportsScraperConfigurationFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LineupScraperTest {

    private final SportsScraperConfigurationFactory sportsScraperConfigurationFactory
            = new SportsScraperConfigurationFactory("./src/main/resources/config/");


    private final LineupScraper lineupScraper = new LineupScraper(sportsScraperConfigurationFactory);

    @Test
    public void lineupScraperTest() throws InterruptedException, JsonProcessingException {
        List<SiteRules> siteRules
                = sportsScraperConfigurationFactory
                .getConfig(ScraperTypeKeys.LINEUPS)
                .getSportScrapers()
                .stream()
                .findFirst()
                .get()
                .getSiteRules();

        assertNotNull(
                lineupScraper.scrape(541766)
                        .getData()
        );

        Thread.sleep(2000L);
    }

}