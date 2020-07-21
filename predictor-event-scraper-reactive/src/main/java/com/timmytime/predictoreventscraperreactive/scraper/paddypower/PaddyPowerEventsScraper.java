package com.timmytime.predictoreventscraperreactive.scraper.paddypower;

import com.timmytime.predictoreventscraperreactive.configuration.BookmakerSiteRules;
import com.timmytime.predictoreventscraperreactive.facade.ScraperProxyFacade;
import com.timmytime.predictoreventscraperreactive.request.ScraperRequest;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class PaddyPowerEventsScraper {

    private final Logger log = LoggerFactory.getLogger(PaddyPowerEventsScraper.class);

    private final ScraperProxyFacade scraperProxyFacade;

    public PaddyPowerEventsScraper(
            ScraperProxyFacade scraperProxyFacade
    ){
        this.scraperProxyFacade = scraperProxyFacade;
    }

    public JSONObject scrape(BookmakerSiteRules siteRules, String appKey){
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Application", appKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject payload = new JSONObject(siteRules.getPayload());
        HttpEntity<?> entity = new HttpEntity<>(payload.toString(), headers);

        log.info("url is {} {}", appKey,  siteRules.getUrl());

        //grab the payload.
        JSONObject response = new JSONObject(
                scraperProxyFacade
                        .scrape(
                                "response",
                                "post",
                                new ScraperRequest(siteRules.getUrl() + "?_ak=" + appKey, entity)).getResponse().toString());

        return response;
    }



}
