package com.timmytime.predictorscraperreactive.util;

import com.timmytime.predictorscraperreactive.enumerator.CompetitionFixtureCodes;
import com.timmytime.predictorscraperreactive.enumerator.ScraperType;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;

public interface IFailedRequests {
    void addFailedResultsRequest(Triple<CompetitionFixtureCodes, ScraperType, String> request);

    void addFailedPlayersRequest(Triple<CompetitionFixtureCodes, ScraperType, String> request);

    List<Triple<CompetitionFixtureCodes, ScraperType, String>> getFailedRequests();

}
