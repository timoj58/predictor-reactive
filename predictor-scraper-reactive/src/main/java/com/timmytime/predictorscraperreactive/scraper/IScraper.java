package com.timmytime.predictorscraperreactive.scraper;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface IScraper<T> {

    T scrape(Integer matchId) throws JsonProcessingException;
}
