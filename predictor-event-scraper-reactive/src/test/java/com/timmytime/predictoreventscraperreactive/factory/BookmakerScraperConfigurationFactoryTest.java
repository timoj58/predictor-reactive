package com.timmytime.predictoreventscraperreactive.factory;

import com.timmytime.predictoreventscraperreactive.enumerator.ScraperTypeKeys;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookmakerScraperConfigurationFactoryTest {

    private final BookmakerScraperConfigurationFactory bookmakerScraperConfigurationFactory
            = new BookmakerScraperConfigurationFactory("./src/main/resources/config/");

    @Test
    public void configTest(){

        assertEquals(1, bookmakerScraperConfigurationFactory.getConfig(ScraperTypeKeys.BETWAY_ODDS).getBookmakerScrapers().size());
        assertEquals(1, bookmakerScraperConfigurationFactory.getConfig(ScraperTypeKeys.PADDYPOWER_ODDS).getBookmakerScrapers().size());


        assertEquals(23,
                bookmakerScraperConfigurationFactory.getConfig(ScraperTypeKeys.BETWAY_ODDS).getBookmakerScrapers()
        .stream()
        .findFirst()
        .get()
        .getSiteRules()
        .stream()
                        .filter(f -> !f.getId().equals("generic") && f.getType().equals("leagues") && f.getActive())
                        .count());


        assertEquals(25,
                bookmakerScraperConfigurationFactory.getConfig(ScraperTypeKeys.PADDYPOWER_ODDS).getBookmakerScrapers()
                        .stream()
                        .findFirst()
                        .get()
                        .getSiteRules()
                        .stream()
                        .filter(f -> f.getType().equals("app-key"))
                        .count());
    }

}