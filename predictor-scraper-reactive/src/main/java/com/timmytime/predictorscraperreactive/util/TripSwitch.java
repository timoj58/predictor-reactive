package com.timmytime.predictorscraperreactive.util;

import com.timmytime.predictorscraperreactive.enumerator.CompetitionFixtureCodes;
import com.timmytime.predictorscraperreactive.enumerator.ScraperType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Component;

import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Slf4j
@Component
public class TripSwitch {

    public static final Integer TRIP_THRESHOLD = 5;
    @Getter
    private final AtomicInteger tripSwitch = new AtomicInteger(TRIP_THRESHOLD);

    public void activate(
            Deque<Triple<CompetitionFixtureCodes, ScraperType, String>> failedResultsRequests,
            Deque<Triple<CompetitionFixtureCodes, ScraperType, String>> failedPlayersRequests,
            Map<CompetitionFixtureCodes, Pair<AtomicInteger, List<String>>> matches
    ) {
        log.info("trip switch activated");

        while (!failedResultsRequests.isEmpty()) {
            var failed = failedResultsRequests.pop();
            log.warn("clearing results: {}, date: {},",
                    failed.getLeft().name().toLowerCase(),
                    failed.getRight());
        }

        while (!failedPlayersRequests.isEmpty()) {
            var failed = failedPlayersRequests.pop();
            log.warn("clearing match: {}, matchId: {},",
                    failed.getLeft().name().toLowerCase(),
                    failed.getRight());
        }

        Stream.of(CompetitionFixtureCodes.values())
                .forEach(key -> {
                    matches.get(key).getRight().clear();
                    matches.get(key).getLeft().set(0);
                });
    }

}
