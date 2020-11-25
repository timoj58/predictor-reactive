package com.timmytime.predictorscraperreactive.scraper;

import com.timmytime.predictorscraperreactive.configuration.SiteRules;
import com.timmytime.predictorscraperreactive.enumerator.ScraperTypeKeys;
import com.timmytime.predictorscraperreactive.factory.SportsScraperConfigurationFactory;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class ResultScraperTest {

    private final SportsScraperConfigurationFactory sportsScraperConfigurationFactory
            = new SportsScraperConfigurationFactory("./src/main/resources/config/");

    private ResultScraper resultScraper
            = new ResultScraper(sportsScraperConfigurationFactory);

    @Test
    public void resultScraperTest() throws InterruptedException {

        LocalDate date = LocalDate.parse("2018-01-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        SiteRules england1 =
                sportsScraperConfigurationFactory.getConfig(ScraperTypeKeys.RESULTS)
                        .getSportScrapers()
                        .stream()
                        .findFirst()
                        .get()
                        .getSiteRules().stream().filter(f -> f.getId().equals("england_1"))
                        .findFirst()
                        .get();

        Thread.sleep(1000);

        assertEquals(5, resultScraper.scrape(england1, date).size());
    }

}