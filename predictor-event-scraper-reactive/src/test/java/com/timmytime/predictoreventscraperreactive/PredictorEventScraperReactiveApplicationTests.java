package com.timmytime.predictoreventscraperreactive;

import com.timmytime.predictoreventscraperreactive.configuration.FixturesScraperConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class PredictorEventScraperReactiveApplicationTests {

    @Autowired
    private FixturesScraperConfiguration fixturesScraperConfiguration;

    @Test
    void contextLoads() {
        assertEquals(25, fixturesScraperConfiguration.getCompetitionFixtures().size());
    }

}
