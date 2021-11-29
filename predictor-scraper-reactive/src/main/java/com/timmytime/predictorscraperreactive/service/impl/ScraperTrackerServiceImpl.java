package com.timmytime.predictorscraperreactive.service.impl;


import com.timmytime.predictorscraperreactive.enumerator.CompetitionFixtureCodes;
import com.timmytime.predictorscraperreactive.enumerator.ScraperType;
import com.timmytime.predictorscraperreactive.enumerator.TrackerQueueAction;
import com.timmytime.predictorscraperreactive.request.Message;
import com.timmytime.predictorscraperreactive.service.MessageService;
import com.timmytime.predictorscraperreactive.service.ScraperTrackerService;
import com.timmytime.predictorscraperreactive.util.TripSwitch;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;


@Slf4j
@Service
public class ScraperTrackerServiceImpl implements ScraperTrackerService {

    private final MessageService messageService;
    private final TripSwitch tripSwitch = new TripSwitch();

    private final Deque<Triple<CompetitionFixtureCodes, ScraperType, String>> failedResultsRequests = new ArrayDeque();
    private final Deque<Triple<CompetitionFixtureCodes, ScraperType, String>> failedPlayersRequests = new ArrayDeque();
    private final Map<CompetitionFixtureCodes, Pair<AtomicInteger, List<String>>> matches = new HashMap<>();

    private final Map<CompetitionFixtureCodes, AtomicBoolean> latch = new HashMap<>();
    private final AtomicInteger previousMessagesSentCount = new AtomicInteger(0);
    private final AtomicInteger requests = new AtomicInteger(0);
    private final AtomicInteger previousRequests = new AtomicInteger(0);
    private final AtomicInteger failedCount = new AtomicInteger(0);
    private final AtomicInteger previousFailedCount = new AtomicInteger(0);
    private final Integer trackerPeriod;
    private Consumer<Triple<CompetitionFixtureCodes, String, TrackerQueueAction>> matchTracker;

    @Autowired
    public ScraperTrackerServiceImpl(
            @Value("${scheduler.tracker}") Integer trackerPeriod,
            MessageService messageService
    ) {
        this.trackerPeriod = trackerPeriod;
        this.messageService = messageService;

        Flux.fromArray(CompetitionFixtureCodes.values())
                .subscribe(competition -> {
                    matches.put(competition, Pair.of(new AtomicInteger(0), new ArrayList<>()));
                    latch.put(competition, new AtomicBoolean(Boolean.FALSE));
                });

        Flux<Triple<CompetitionFixtureCodes, String, TrackerQueueAction>> trackerQueue = Flux.create(sink ->
                ScraperTrackerServiceImpl.this.matchTracker = sink::next, FluxSink.OverflowStrategy.BUFFER);

        trackerQueue.limitRate(1).subscribe(this::trackerQueueHandler);
    }

    @Override
    public void addFailedResultsRequest(Triple<CompetitionFixtureCodes, ScraperType, String> request) {
        if (tripSwitch.getTripSwitch().get() >= 0) {
            failedCount.incrementAndGet();
            failedResultsRequests.add(request);
        }
    }

    @Override
    public void addFailedPlayersRequest(Triple<CompetitionFixtureCodes, ScraperType, String> request) {
        if (tripSwitch.getTripSwitch().get() >= 0) {
            failedCount.incrementAndGet();
            failedPlayersRequests.add(request);
        }
    }

    @Override
    public List<Triple<CompetitionFixtureCodes, ScraperType, String>> getFailedRequests() {
        log.info("iteration failed results count {}", failedResultsRequests.size());
        log.info("iteration failed player count {}", failedPlayersRequests.size());

        if (tripSwitch.getTripSwitch().get() < 0) {
            return Collections.emptyList();
        }

        List<Triple<CompetitionFixtureCodes, ScraperType, String>> popped = new ArrayList<>();

        while (!failedResultsRequests.isEmpty()) {
            popped.add(failedResultsRequests.pop());
        }

        while (!failedPlayersRequests.isEmpty()) {
            popped.add(failedPlayersRequests.pop());
        }

        return popped;
    }

    @Override
    public void addResultsInQueue(CompetitionFixtureCodes competition, int total) {
        matchTracker.accept(Triple.of(competition, String.valueOf(total), TrackerQueueAction.UPDATE_IN_QUEUE));
    }

    @Override
    public void removeResultsFromQueue(CompetitionFixtureCodes competition) {
        matchTracker.accept(Triple.of(competition, null, TrackerQueueAction.REMOVE_IN_QUEUE));
    }

    @Override
    public void addMatch(Pair<CompetitionFixtureCodes, String> matchRequest) {
        matchTracker.accept(Triple.of(matchRequest.getLeft(), matchRequest.getRight(), TrackerQueueAction.ADD_MATCH));
    }

    @Override
    public void removeMatch(Pair<CompetitionFixtureCodes, String> matchRequest) {
        matchTracker.accept(Triple.of(matchRequest.getLeft(), matchRequest.getRight(), TrackerQueueAction.REMOVE_MATCH));
    }

    @Override
    public void incrementRequest() {
        requests.incrementAndGet();
    }


    private void trackerQueueHandler(Triple<CompetitionFixtureCodes, String, TrackerQueueAction> item) {
        if (tripSwitch.getTripSwitch().get() >= 0) {
            switch (item.getRight()) {
                case ADD_MATCH:
                    matches.get(item.getLeft()).getRight().add(item.getMiddle());
                    break;
                case REMOVE_MATCH:
                    matches.get(item.getLeft()).getRight().remove(item.getMiddle());
                    break;
                case UPDATE_IN_QUEUE:
                    matches.get(item.getLeft()).getLeft().getAndAdd(Integer.parseInt(item.getMiddle()));
                    if (latch.get(item.getLeft()).get() == Boolean.FALSE) {
                        latch.get(item.getLeft()).set(Boolean.TRUE);
                    }
                    break;
                case REMOVE_IN_QUEUE:
                    matches.get(item.getLeft()).getLeft().decrementAndGet();
                    break;
            }
        }
    }

    @Scheduled(fixedRateString = "${scheduler.tracker}")
    private void tracker() {

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

        var messageSentCount = messageService.getMessagesSentCount();
        var latchStatus = latch.values().stream().allMatch(AtomicBoolean::get);
        var queueTotal = matches.values().stream().map(Pair::getLeft).mapToInt(AtomicInteger::get).sum();

        log.info("messagesSent: {}, latchStatus: {}, queueTotal: {}", messageSentCount, latchStatus, queueTotal);

        if (latchStatus && messageSentCount == previousMessagesSentCount.get() && queueTotal < 5) {
            log.info("trip activated {}", tripSwitch.getTripSwitch().decrementAndGet());
            if (tripSwitch.getTripSwitch().get() < 0) {
                tripSwitch.tripSwitchActivated(
                        failedResultsRequests,
                        failedPlayersRequests,
                        matches
                );
            }
        } else if (latchStatus && messageSentCount != previousMessagesSentCount.get() && tripSwitch.getTripSwitch().get() != TripSwitch.TRIP_THRESHOLD) {
            log.info("trip reset");
            tripSwitch.getTripSwitch().set(TripSwitch.TRIP_THRESHOLD);
        }

        matches.keySet().forEach(
                key -> {
                    var info = matches.get(key);
                    var competitionLatch = latch.get(key).get();
                    if (competitionLatch) {
                        log.info("{} => resultsQueue: {}, outstandingMatches: {}",
                                key.name().toLowerCase(),
                                info.getLeft().get(),
                                info.getRight().size());

                        if (info.getLeft().get() == 0 && info.getRight().isEmpty()) {
                            latch.get(key).set(Boolean.FALSE);
                            messageService.send(new Message(key.name().toLowerCase()));
                        }
                    }
                }
        );

        previousMessagesSentCount.set(messageSentCount);
        previousRequests.set(requests.get());
        previousFailedCount.set(failedCount.get());
    }


}
