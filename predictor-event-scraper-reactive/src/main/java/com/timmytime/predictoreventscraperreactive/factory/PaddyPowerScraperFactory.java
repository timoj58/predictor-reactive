package com.timmytime.predictoreventscraperreactive.factory;

import com.timmytime.predictoreventscraperreactive.facade.ScraperProxyFacade;
import com.timmytime.predictoreventscraperreactive.scraper.paddypower.PaddyPowerAppKeyScraper;
import com.timmytime.predictoreventscraperreactive.scraper.paddypower.PaddyPowerEventSpecificScraper;
import com.timmytime.predictoreventscraperreactive.scraper.paddypower.PaddyPowerEventsScraper;
import org.springframework.stereotype.Component;

@Component
public class PaddyPowerScraperFactory {

    public PaddyPowerAppKeyScraper getAppKeyScraper(
            ScraperProxyFacade scraperProxyFacade
    ){
        return new PaddyPowerAppKeyScraper(scraperProxyFacade);
    }

    public PaddyPowerEventsScraper getEventsScraper(
            ScraperProxyFacade scraperProxyFacade
    ){
        return new PaddyPowerEventsScraper(scraperProxyFacade);
    }

    public PaddyPowerEventSpecificScraper getEventScraper(
            ScraperProxyFacade scraperProxyFacade
    ){
        return new PaddyPowerEventSpecificScraper(scraperProxyFacade);
    }
}
