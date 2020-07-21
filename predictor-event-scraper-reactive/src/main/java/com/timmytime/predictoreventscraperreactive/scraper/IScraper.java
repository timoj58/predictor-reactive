package com.timmytime.predictoreventscraperreactive.scraper;

import com.timmytime.predictoreventscraperreactive.configuration.BookmakerSiteRules;
import com.timmytime.predictoreventscraperreactive.model.ScraperModel;
import org.json.JSONObject;

public interface IScraper {
    ScraperModel scrape(BookmakerSiteRules bookmakerSiteRules, JSONObject event, String competition);
}
