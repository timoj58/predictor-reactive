package com.timmytime.predictorscraperreactive.configuration;

import com.timmytime.predictorscraperreactive.enumerator.CompetitionFixtureCodes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ResultsConfigurationTest {

    private final ResultsConfiguration resultsConfiguration =
            new ResultsConfiguration("https://www.espn.co.uk/soccer/scoreboard/_/league/{league}/date/{date}");


    @Test
    void loadedTest() {
        assertTrue(
                resultsConfiguration.getUrls().size() == CompetitionFixtureCodes.values().length
        );
    }
}