package com.timmytime.predictoreventscraperreactive.factory;

import com.timmytime.predictoreventscraperreactive.scraper.betway.BetwayEventSpecificScraper;
import com.timmytime.predictoreventscraperreactive.scraper.betway.BetwayEventsScraper;
import org.springframework.stereotype.Component;

@Component
public class BetwayScraperFactory {

    public BetwayEventsScraper getEventsScraper(){
        return new BetwayEventsScraper();
    }

    public BetwayEventSpecificScraper getEventScraper(){
        return new BetwayEventSpecificScraper();
    }
}
