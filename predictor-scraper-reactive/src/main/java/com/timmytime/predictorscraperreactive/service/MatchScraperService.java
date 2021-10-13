package com.timmytime.predictorscraperreactive.service;

import com.timmytime.predictorscraperreactive.enumerator.CompetitionFixtureCodes;
import org.apache.commons.lang3.tuple.Pair;

public interface MatchScraperService {
    void add(Pair<CompetitionFixtureCodes, Integer> matchId);
}
