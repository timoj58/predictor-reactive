package com.timmytime.predictorscraperreactive.service.impl;


import com.timmytime.predictorscraperreactive.enumerator.CompetitionFixtureCodes;
import com.timmytime.predictorscraperreactive.enumerator.ScraperType;
import com.timmytime.predictorscraperreactive.enumerator.TrackerQueueAction;
import com.timmytime.predictorscraperreactive.service.MessageService;
import com.timmytime.predictorscraperreactive.service.ScraperTrackerService;
import com.timmytime.predictorscraperreactive.util.MatchTracker;
import com.timmytime.predictorscraperreactive.util.FailedRequestTracker;
import com.timmytime.predictorscraperreactive.util.TrackerMetrics;
import com.timmytime.predictorscraperreactive.util.TripSwitch;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static com.timmytime.predictorscraperreactive.util.TripSwitch.TRIP_THRESHOLD;


@Slf4j
@Service
public class ScraperTrackerServiceImpl implements ScraperTrackerService {

    private final MessageService messageService;
    private final TrackerMetrics trackerMetrics;
    private final TripSwitch tripSwitch;
    private final MatchTracker matchTracker;
    private final FailedRequestTracker failedRequestTracker;

    private final AtomicInteger previousMessagesSentCount = new AtomicInteger(0);
    private Consumer<Triple<CompetitionFixtureCodes, String, TrackerQueueAction>> matchConsumer;

    @Autowired
    public ScraperTrackerServiceImpl(
            MessageService messageService,
            TrackerMetrics trackerMetrics,
            TripSwitch tripSwitch,
            MatchTracker matchTracker,
            FailedRequestTracker failedRequestTracker
    ) {
        this.messageService = messageService;
        this.trackerMetrics = trackerMetrics;
        this.tripSwitch = tripSwitch;
        this.matchTracker = matchTracker;
        this.failedRequestTracker = failedRequestTracker;

        Flux<Triple<CompetitionFixtureCodes, String, TrackerQueueAction>> trackerQueue = Flux.create(sink ->
                ScraperTrackerServiceImpl.this.matchConsumer = sink::next, FluxSink.OverflowStrategy.BUFFER);

        trackerQueue.limitRate(1).subscribe(item -> {
            if (tripSwitch.getTripSwitch().get() >= 0)
                matchTracker.handle(item);
        });
    }

    @Override
    public void addFailedResultsRequest(Triple<CompetitionFixtureCodes, ScraperType, String> request) {
        if (tripSwitch.getTripSwitch().get() >= 0) {
            failedRequestTracker.addFailedResultsRequest(request);
            trackerMetrics.addFailedResultsRequest(request);
        }
    }

    @Override
    public void addFailedPlayersRequest(Triple<CompetitionFixtureCodes, ScraperType, String> request) {
        if (tripSwitch.getTripSwitch().get() >= 0) {
            failedRequestTracker.addFailedPlayersRequest(request);
            trackerMetrics.addFailedPlayersRequest(request);
        }
    }

    @Override
    public List<Triple<CompetitionFixtureCodes, ScraperType, String>> getFailedRequests() {
        if (tripSwitch.getTripSwitch().get() < 0)
            return Collections.emptyList();

        return failedRequestTracker.getFailedRequests();
    }

    @Override
    public void addResultsInQueue(CompetitionFixtureCodes competition, int total) {
        matchConsumer.accept(Triple.of(competition, String.valueOf(total), TrackerQueueAction.UPDATE_IN_QUEUE));
    }

    @Override
    public void removeResultsFromQueue(CompetitionFixtureCodes competition) {
        matchConsumer.accept(Triple.of(competition, null, TrackerQueueAction.REMOVE_IN_QUEUE));
    }

    @Override
    public void addMatch(Pair<CompetitionFixtureCodes, String> matchRequest) {
        matchConsumer.accept(Triple.of(matchRequest.getLeft(), matchRequest.getRight(), TrackerQueueAction.ADD_MATCH));
    }

    @Override
    public void removeMatch(Pair<CompetitionFixtureCodes, String> matchRequest) {
        matchConsumer.accept(Triple.of(matchRequest.getLeft(), matchRequest.getRight(), TrackerQueueAction.REMOVE_MATCH));
    }

    @Override
    public void incrementRequest() {
        trackerMetrics.incrementRequest();
    }


    @Scheduled(fixedRateString = "${scheduler.tracker}")
    private void tracker() {
        var messageSentCount = messageService.getMessagesSentCount();
        CompletableFuture.runAsync(trackerMetrics::calculate)
                .thenRun(() -> {
                    var stats = matchTracker.calculate(messageSentCount);
                    var latchStatus = stats.getLeft();
                    var queueTotal = stats.getRight();

                    if (latchStatus && messageSentCount == previousMessagesSentCount.get() && queueTotal < TRIP_THRESHOLD) {
                        log.info("trip activated {}", tripSwitch.getTripSwitch().decrementAndGet());
                        if (tripSwitch.getTripSwitch().get() < 0) {
                            tripSwitch.activate(
                                    failedRequestTracker.getFailedResultsRequests(),
                                    failedRequestTracker.getFailedPlayersRequests(),
                                    matchTracker.getMatches()
                            );
                        }
                    } else if (latchStatus && messageSentCount != previousMessagesSentCount.get() && tripSwitch.getTripSwitch().get() != TRIP_THRESHOLD) {
                        log.info("trip reset");
                        tripSwitch.getTripSwitch().set(TRIP_THRESHOLD);
                    }

                    matchTracker.testFinished(messageService::send);
                    previousMessagesSentCount.set(messageSentCount);
                });

    }


}
