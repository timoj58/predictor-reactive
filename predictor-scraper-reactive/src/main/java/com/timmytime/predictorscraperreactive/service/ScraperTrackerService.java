package com.timmytime.predictorscraperreactive.service;

import com.timmytime.predictorscraperreactive.enumerator.CompetitionFixtureCodes;
import com.timmytime.predictorscraperreactive.enumerator.ScraperType;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;

public interface ScraperTrackerService {
    void addFailedResultsRequest(Triple<CompetitionFixtureCodes, ScraperType, String> request);

    void addFailedPlayersRequest(Triple<CompetitionFixtureCodes, ScraperType, String> request);

    List<Triple<CompetitionFixtureCodes, ScraperType, String>> getFailedResultsRequests();

    List<Triple<CompetitionFixtureCodes, ScraperType, String>> getFailedPlayersRequests();

    void addResultsInQueue(CompetitionFixtureCodes competition, int total);

    void removeResultsFromQueue(CompetitionFixtureCodes competition);

    void addMatch(Pair<CompetitionFixtureCodes, String> matchRequest);

    void removeMatch(Pair<CompetitionFixtureCodes, String> matchRequest);

    public void incrementRequest();

}
