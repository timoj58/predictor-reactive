package com.timmytime.predictoreventscraperreactive.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class BookmakerScraper {

    @JsonProperty
    private List<BookmakerSiteRules> siteRules;

    public BookmakerScraper() {

    }

    public List<BookmakerSiteRules> getSiteRules() {
        return siteRules;
    }

    public void setSiteRules(List<BookmakerSiteRules> siteRules) {
        this.siteRules = siteRules;
    }


}
