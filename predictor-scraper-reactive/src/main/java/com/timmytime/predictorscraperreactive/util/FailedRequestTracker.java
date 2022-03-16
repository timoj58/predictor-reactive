package com.timmytime.predictorscraperreactive.util;

import com.timmytime.predictorscraperreactive.enumerator.CompetitionFixtureCodes;
import com.timmytime.predictorscraperreactive.enumerator.ScraperType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

@Slf4j
@Component
public class FailedRequestTracker implements IFailedRequests {
    @Getter
    private final Deque<Triple<CompetitionFixtureCodes, ScraperType, String>> failedResultsRequests = new ArrayDeque();
    @Getter
    private final Deque<Triple<CompetitionFixtureCodes, ScraperType, String>> failedPlayersRequests = new ArrayDeque();

    @Override
    public void addFailedResultsRequest(Triple<CompetitionFixtureCodes, ScraperType, String> request) {
        failedResultsRequests.add(request);
    }

    @Override
    public void addFailedPlayersRequest(Triple<CompetitionFixtureCodes, ScraperType, String> request) {
        failedPlayersRequests.add(request);
    }

    @Override
    public List<Triple<CompetitionFixtureCodes, ScraperType, String>> getFailedRequests() {
        log.info("iteration failed results count {}", failedResultsRequests.size());
        log.info("iteration failed player count {}", failedPlayersRequests.size());

        List<Triple<CompetitionFixtureCodes, ScraperType, String>> popped = new ArrayList<>();

        while (!failedResultsRequests.isEmpty()) {
            popped.add(failedResultsRequests.pop());
        }

        while (!failedPlayersRequests.isEmpty()) {
            popped.add(failedPlayersRequests.pop());
        }

        return popped;
    }
}
