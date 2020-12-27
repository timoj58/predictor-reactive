package com.timmytime.predictorscraperreactive.factory;

import com.timmytime.predictorscraperreactive.model.Lineup;
import com.timmytime.predictorscraperreactive.model.Match;
import com.timmytime.predictorscraperreactive.scraper.*;
import org.springframework.stereotype.Component;

@Component
public class ScraperFactory {

    public ResultScraper getResultScraper(
            SportsScraperConfigurationFactory sportsScraperConfigurationFactory
    ) {
        return new ResultScraper(sportsScraperConfigurationFactory);
    }

    public IScraper<Lineup> getLineupScraper(
            SportsScraperConfigurationFactory sportsScraperConfigurationFactory
    ) {
        return new LineupScraper(sportsScraperConfigurationFactory);
    }

    public IScraper<Match> getMatchScraper(
            SportsScraperConfigurationFactory sportsScraperConfigurationFactory
    ) {
        return new MatchScraper(sportsScraperConfigurationFactory);
    }

    public IScraper<Lineup> getPlayerStatsScraper(
            SportsScraperConfigurationFactory sportsScraperConfigurationFactory,
            Lineup lineup
    ) {
        return new PlayerStatsScraper(sportsScraperConfigurationFactory, lineup);
    }
}
