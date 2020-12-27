package com.timmytime.predictorscraperreactive.factory;

import com.timmytime.predictorscraperreactive.configuration.SiteRules;
import com.timmytime.predictorscraperreactive.enumerator.ScraperTypeKeys;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SportsScraperConfigurationFactoryTest {

    private final SportsScraperConfigurationFactory
            sportsScraperConfigurationFactory = new
            SportsScraperConfigurationFactory(
            "./src/main/resources/config/");

    @Test
    public void loadTests() {


        assertEquals(1, sportsScraperConfigurationFactory.getConfig(ScraperTypeKeys.RESULTS).getSportScrapers().size());
        assertEquals(1, sportsScraperConfigurationFactory.getConfig(ScraperTypeKeys.MATCHES).getSportScrapers().size());
        assertEquals(1, sportsScraperConfigurationFactory.getConfig(ScraperTypeKeys.PLAYER_STATS).getSportScrapers().size());
        assertEquals(1, sportsScraperConfigurationFactory.getConfig(ScraperTypeKeys.LINEUPS).getSportScrapers().size());

        assertEquals(1, sportsScraperConfigurationFactory
                .getConfig(ScraperTypeKeys.RESULTS)
                .getSportScrapers()
                .stream()
                .findFirst()
                .get()
                .getSiteRules()
                .stream()
                .filter(f -> !f.getId().equals("generic"))
                .sorted(Comparator.comparing(SiteRules::getOrder))
                .findFirst()
                .get()
                .getOrder());
    }

}