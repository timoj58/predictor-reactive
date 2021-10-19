package com.timmytime.predictorscraperreactive.service;

import java.time.LocalDateTime;

public interface CompetitionScraperService {
    void scrape(LocalDateTime date);

    void setResultsInQueue(int total);
}
