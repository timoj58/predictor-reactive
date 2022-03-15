package com.timmytime.predictorscraperreactive.util;

import com.timmytime.predictorscraperreactive.enumerator.CompetitionFixtureCodes;
import com.timmytime.predictorscraperreactive.enumerator.ScraperType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class TrackerMetrics implements IRequests {

    private final Integer trackerPeriod;
    private final AtomicInteger requests = new AtomicInteger(0);
    private final AtomicInteger previousRequests = new AtomicInteger(0);
    private final AtomicInteger failedCount = new AtomicInteger(0);
    private final AtomicInteger previousFailedCount = new AtomicInteger(0);

    @Autowired
    public TrackerMetrics(
            @Value("${scheduler.tracker}") Integer trackerPeriod
    ) {
        this.trackerPeriod = trackerPeriod;
    }

    public void calculate() {
        if (requests.get() > 0 && previousRequests.get() > 0) {
            var requestsInInterval = requests.get() - previousRequests.get();
            var failedRequestsInterval = failedCount.get() - previousFailedCount.get();
            var requestSuccess = 0.0;
            if (requestsInInterval > 0) {
                requestSuccess = (100.0 - ((double) failedRequestsInterval / (double) requestsInInterval) * 100.0);
            }
            log.info("requestsPerSecond: {}, success: {}%",
                    String.format("%.2f", (requestsInInterval / (trackerPeriod / 1000.0))),
                    String.format("%.2f", requestSuccess));
        }

        previousRequests.set(requests.get());
        previousFailedCount.set(failedCount.get());
    }

    @Override
    public void addFailedResultsRequest(Triple<CompetitionFixtureCodes, ScraperType, String> request) {
        failedCount.incrementAndGet();

    }

    @Override
    public void addFailedPlayersRequest(Triple<CompetitionFixtureCodes, ScraperType, String> request) {
        failedCount.incrementAndGet();
    }

    @Override
    public List<Triple<CompetitionFixtureCodes, ScraperType, String>> getFailedRequests() {
        return null;
    }

    @Override
    public void incrementRequest() {
        requests.incrementAndGet();
    }
}
