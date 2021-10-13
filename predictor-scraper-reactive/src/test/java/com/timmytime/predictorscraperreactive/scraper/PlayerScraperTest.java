package com.timmytime.predictorscraperreactive.scraper;

import com.timmytime.predictorscraperreactive.enumerator.CompetitionFixtureCodes;
import com.timmytime.predictorscraperreactive.service.ScraperTrackerService;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

public class PlayerScraperTest {


    PlayerScraper playerScraper = new PlayerScraper("https://www.espn.co.uk/football/match/_/gameId/{game_id}",
            mock(ScraperTrackerService.class));

    @Test
    void scrapeTest() {
        playerScraper.scrape(Pair.of(CompetitionFixtureCodes.BELGIUM_1, 605973));
    }
}
