package com.timmytime.predictorscraperreactive.scraper;

import org.junit.jupiter.api.Test;

public class PlayerScraperTest {

    private final PlayerScraper playerScraper = new PlayerScraper("https://www.espn.co.uk/football/match/_/gameId/{game_id}");

    @Test
    void scrapeTest() {
        //    playerScraper.scrape(Pair.of(CompetitionFixtureCodes.ITALY_1, 278345));
    }

}
