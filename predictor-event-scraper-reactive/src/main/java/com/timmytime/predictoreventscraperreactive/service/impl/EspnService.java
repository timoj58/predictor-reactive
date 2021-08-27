package com.timmytime.predictoreventscraperreactive.service.impl;

import com.timmytime.predictoreventscraperreactive.configuration.FixturesScraperConfiguration;
import com.timmytime.predictoreventscraperreactive.scraper.CompetitionFixtureScraper;
import com.timmytime.predictoreventscraperreactive.service.BookmakerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Slf4j
@Service
public class EspnService implements BookmakerService {

    private final FixturesScraperConfiguration fixturesScraperConfiguration;
    private final CompetitionFixtureScraper competitionFixtureScraper;

    @Autowired
    public EspnService(
            FixturesScraperConfiguration fixturesScraperConfiguration,
            CompetitionFixtureScraper competitionFixtureScraper
    ) {
        this.fixturesScraperConfiguration = fixturesScraperConfiguration;
        this.competitionFixtureScraper = competitionFixtureScraper;
    }

    @Override
    public void scrape() {
        Flux.fromStream(fixturesScraperConfiguration.getCompetitionFixtures().stream()
        ).delayElements(Duration.ofMillis(100)).limitRate(4).subscribe(competitionFixtureScraper::scrape);
    }
}
