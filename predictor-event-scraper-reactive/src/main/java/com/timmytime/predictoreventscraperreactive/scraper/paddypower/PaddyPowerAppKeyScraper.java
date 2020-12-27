package com.timmytime.predictoreventscraperreactive.scraper.paddypower;

import com.timmytime.predictoreventscraperreactive.configuration.BookmakerSiteRules;
import com.timmytime.predictoreventscraperreactive.facade.ScraperProxyFacade;
import com.timmytime.predictoreventscraperreactive.request.ScraperRequest;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class PaddyPowerAppKeyScraper {

    private final ScraperProxyFacade scraperProxyFacade;

    public String scrape(BookmakerSiteRules siteRules) {

        JSONObject key = new JSONObject(siteRules.getKeys().get(0));

        log.info("url {}", siteRules.getUrl());

        //grab the payload.
        String response =
                scraperProxyFacade.scrape("document", "get", new ScraperRequest(siteRules.getUrl(), null)).getDocument();


        Document document = Parser.htmlParser().parseInput(response, "");

        Pattern pattern = Pattern.compile("(?<=" + key.getString("key") + ": \")(.*)(?=\",)");
        Matcher matcher = pattern.matcher(document.body().toString());

        String appKey = "";

        while (matcher.find()) {
            appKey = matcher.group(1);
        }

        return appKey;
    }

    ;

    @Autowired
    public PaddyPowerAppKeyScraper(ScraperProxyFacade scraperProxyFacade) {
        this.scraperProxyFacade = scraperProxyFacade;
    }

}
