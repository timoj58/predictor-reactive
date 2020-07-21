package com.timmytime.predictorscraperreactive.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SportScraperConfiguration {

    @JsonProperty
    private List<SportScraper> sportsScrapers;

    public SportScraperConfiguration() {

    }

    public List<SportScraper> getSportScrapers() {
        return sportsScrapers;
    }

    public void setSportScrapers(List<SportScraper> sportsScrapers) {
        this.sportsScrapers = sportsScrapers;
    }
}
