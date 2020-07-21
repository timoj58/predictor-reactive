package com.timmytime.predictorscraperreactive.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SportScraper {

    @JsonProperty
    private List<SiteRules> siteRules;

}
