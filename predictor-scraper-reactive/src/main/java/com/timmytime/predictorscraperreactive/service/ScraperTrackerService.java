package com.timmytime.predictorscraperreactive.service;

import com.timmytime.predictorscraperreactive.enumerator.CompetitionFixtureCodes;
import com.timmytime.predictorscraperreactive.util.IRequests;
import org.apache.commons.lang3.tuple.Pair;

public interface ScraperTrackerService extends IRequests {

    void addResultsInQueue(CompetitionFixtureCodes competition, int total);

    void removeResultsFromQueue(CompetitionFixtureCodes competition);

    void addMatch(Pair<CompetitionFixtureCodes, String> matchRequest);

    void removeMatch(Pair<CompetitionFixtureCodes, String> matchRequest);

}
