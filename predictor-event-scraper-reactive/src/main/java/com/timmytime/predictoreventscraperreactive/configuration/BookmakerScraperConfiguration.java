package com.timmytime.predictoreventscraperreactive.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookmakerScraperConfiguration {

    @JsonProperty
    private List<BookmakerScraper> bookmakerScrapers;

    public BookmakerScraperConfiguration() {

    }

    public List<BookmakerScraper> getBookmakerScrapers() {
        return bookmakerScrapers;
    }

    public void setBookmakerScrapers(List<BookmakerScraper> bookmakerScrapers) {
        this.bookmakerScrapers = bookmakerScrapers;
    }

}
