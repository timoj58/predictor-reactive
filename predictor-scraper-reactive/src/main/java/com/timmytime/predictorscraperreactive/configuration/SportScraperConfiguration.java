package com.timmytime.predictorscraperreactive.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor
public class SportScraperConfiguration {

    @JsonProperty
    private List<SportScraper> sportsScrapers;

    public List<SportScraper> getSportScrapers() {
        return sportsScrapers;
    }

    public void setSportScrapers(List<SportScraper> sportsScrapers) {
        this.sportsScrapers = sportsScrapers;
    }
}
