package com.timmytime.predictorscraperreactive.factory;

import com.timmytime.predictorscraperreactive.facade.WebClientFacade;
import com.timmytime.predictorscraperreactive.scraper.PlayerScraper;
import com.timmytime.predictorscraperreactive.scraper.ResultScraper;
import com.timmytime.predictorscraperreactive.service.ScraperTrackerService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ScraperFactory {

    private final String matchUrl;
    @Getter
    private final ScraperTrackerService scraperTrackerService;
    private final WebClientFacade webClientFacade;

    @Autowired
    public ScraperFactory(
            @Value("${scraper.match}") String matchUrl,
            ScraperTrackerService scraperTrackerService,
            WebClientFacade webClientFacade) {
        this.matchUrl = matchUrl;
        this.scraperTrackerService = scraperTrackerService;
        this.webClientFacade = webClientFacade;
    }

    public ResultScraper getResultScraper() {
        return new ResultScraper();
    }

    public PlayerScraper getPlayerScraper() {
        return new PlayerScraper(matchUrl);
    }

}
