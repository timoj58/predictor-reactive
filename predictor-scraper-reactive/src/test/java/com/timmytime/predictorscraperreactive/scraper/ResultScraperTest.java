package com.timmytime.predictorscraperreactive.scraper;

import com.timmytime.predictorscraperreactive.enumerator.CompetitionFixtureCodes;
import com.timmytime.predictorscraperreactive.model.Result;
import com.timmytime.predictorscraperreactive.service.ScraperTrackerService;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class ResultScraperTest {

    private final ResultScraper resultScraper = new ResultScraper(mock(ScraperTrackerService.class));

    @Test
    void scrapeTest() {
        List<Result> results = resultScraper.scrape(
                Pair.of(CompetitionFixtureCodes.ENGLAND_1,
                        "https://www.espn.co.uk/soccer/scoreboard/_/league/eng.1/date/{date}"),
                LocalDate.parse("03-10-2021", DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        );

        assertEquals(4, results.size());
    }

}