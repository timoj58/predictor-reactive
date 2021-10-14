package com.timmytime.predictorscraperreactive.service;

import com.timmytime.predictorscraperreactive.enumerator.CompetitionFixtureCodes;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.time.LocalDate;
import java.util.List;

public interface ScraperTrackerService {
    void addFailedResultsRequest(Pair<CompetitionFixtureCodes, String> competition, LocalDate date);

    void addFailedPlayersRequest(Pair<CompetitionFixtureCodes, Integer> matchId);

    List<Triple<CompetitionFixtureCodes, String, LocalDate>> getFailedResultsRequests();

    List<Pair<CompetitionFixtureCodes, Integer>> getFailedPlayersRequests();

    void addMatchesInQueue(CompetitionFixtureCodes competition);

    void removeMatchesFromQueue(CompetitionFixtureCodes competition);

    void addMatches(CompetitionFixtureCodes competition, List<Integer> matchIds);

    void removeMatch(Pair<CompetitionFixtureCodes, Integer> matchId);

}
