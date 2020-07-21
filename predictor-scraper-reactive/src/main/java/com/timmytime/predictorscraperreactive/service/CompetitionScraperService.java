package com.timmytime.predictorscraperreactive.service;

import com.timmytime.predictorscraperreactive.configuration.SiteRules;
import com.timmytime.predictorscraperreactive.model.ScraperHistory;

public interface CompetitionScraperService {
    void scrape(ScraperHistory scraperHistory, SiteRules competition);
}
