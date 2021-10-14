package com.timmytime.predictorscraperreactive.service.impl;


import com.timmytime.predictorscraperreactive.enumerator.CompetitionFixtureCodes;
import com.timmytime.predictorscraperreactive.enumerator.TrackerQueueAction;
import com.timmytime.predictorscraperreactive.request.Message;
import com.timmytime.predictorscraperreactive.service.MessageService;
import com.timmytime.predictorscraperreactive.service.ScraperTrackerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Slf4j
@Service
public class ScraperTrackerServiceImpl implements ScraperTrackerService {

    private static final Integer TRIP_THRESHOLD = 5;  //not for historic usage.
    private final MessageService messageService;

    private final Deque<Triple<CompetitionFixtureCodes, String, LocalDate>> failedResultsRequests = new ArrayDeque();
    private final Deque<Pair<CompetitionFixtureCodes, Integer>> failedPlayersRequests = new ArrayDeque();
    private final Map<CompetitionFixtureCodes, Pair<AtomicInteger, List<Integer>>> matches = new HashMap<>();

    private final Map<CompetitionFixtureCodes, AtomicBoolean> latch = new HashMap<>();
    private final AtomicInteger previousMessagesSentCount = new AtomicInteger(0);
    private final AtomicInteger tripSwitch = new AtomicInteger(TRIP_THRESHOLD);

    private Consumer<Triple<CompetitionFixtureCodes, Integer, TrackerQueueAction>> matchTracker;

    @Autowired
    public ScraperTrackerServiceImpl(
            MessageService messageService
    ) {
        this.messageService = messageService;

        Flux.fromArray(CompetitionFixtureCodes.values())
                .subscribe(competition -> {
                    matches.put(competition, Pair.of(new AtomicInteger(0), new ArrayList<>()));
                    latch.put(competition, new AtomicBoolean(Boolean.FALSE));
                });

        Flux<Triple<CompetitionFixtureCodes, Integer, TrackerQueueAction>> trackerQueue = Flux.push(sink ->
                ScraperTrackerServiceImpl.this.matchTracker = sink::next, FluxSink.OverflowStrategy.BUFFER);

        trackerQueue.limitRate(1).subscribe(this::trackerQueueHandler);
    }

    @Override
    public void addFailedResultsRequest(Pair<CompetitionFixtureCodes, String> competition, LocalDate date) {
        if (tripSwitch.get() >= 0) {
            failedResultsRequests.add(Triple.of(competition.getLeft(), competition.getRight(), date));
        }
    }

    @Override
    public void addFailedPlayersRequest(Pair<CompetitionFixtureCodes, Integer> matchId) {
        if (tripSwitch.get() >= 0) {
            failedPlayersRequests.add(matchId);
        }
    }

    @Override
    public List<Triple<CompetitionFixtureCodes, String, LocalDate>> getFailedResultsRequests() {
        log.info("iteration failed results count {}", failedResultsRequests.size());
        if (tripSwitch.get() < 0) {
            return Collections.emptyList();
        }

        List<Triple<CompetitionFixtureCodes, String, LocalDate>> popped = new ArrayList<>();

        while (!failedResultsRequests.isEmpty()) {
            popped.add(failedResultsRequests.pop());
        }

        return popped;
    }

    @Override
    public List<Pair<CompetitionFixtureCodes, Integer>> getFailedPlayersRequests() {
        log.info("iteration failed player count {}", failedPlayersRequests.size());
        if (tripSwitch.get() < 0) {
            return Collections.emptyList();
        }

        List<Pair<CompetitionFixtureCodes, Integer>> popped = new ArrayList<>();

        while (!failedPlayersRequests.isEmpty()) {
            popped.add(failedPlayersRequests.pop());
        }
        return popped;
    }

    @Override
    public void addMatchesInQueue(CompetitionFixtureCodes competition) {
        matchTracker.accept(Triple.of(competition, null, TrackerQueueAction.UPDATE_IN_QUEUE));
    }

    @Override
    public void removeMatchesFromQueue(CompetitionFixtureCodes competition) {
        matchTracker.accept(Triple.of(competition, null, TrackerQueueAction.REMOVE_IN_QUEUE));
    }

    @Override
    public void addMatches(CompetitionFixtureCodes competition, List<Integer> matchIds) {
        matchIds.forEach(id -> matchTracker.accept(Triple.of(competition, id, TrackerQueueAction.ADD_MATCH)));
    }

    @Override
    public void removeMatch(Pair<CompetitionFixtureCodes, Integer> matchId) {
        matchTracker.accept(Triple.of(matchId.getLeft(), matchId.getRight(), TrackerQueueAction.REMOVE_MATCH));
    }


    private void trackerQueueHandler(Triple<CompetitionFixtureCodes, Integer, TrackerQueueAction> item) {
        if (tripSwitch.get() >= 0) {
            switch (item.getRight()) {
                case ADD_MATCH:
                    matches.get(item.getLeft()).getRight().add(item.getMiddle());
                    break;
                case REMOVE_MATCH:
                    matches.get(item.getLeft()).getRight().remove(item.getMiddle());
                    break;
                case UPDATE_IN_QUEUE:
                    matches.get(item.getLeft()).getLeft().incrementAndGet();
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

    @Scheduled(fixedRate = 48000)
    private void tracker() {
        var messageSentCount = messageService.getMessagesSentCount();
        var latchStatus = latch.values().stream().allMatch(AtomicBoolean::get);

        if (latchStatus && messageSentCount == previousMessagesSentCount.get()) {
            log.info("trip activated {}", tripSwitch.decrementAndGet());
            if(tripSwitch.get() < 0){
                tripSwitchActivated();
            }
        } else if (latchStatus && messageSentCount != previousMessagesSentCount.get()) {
            log.info("trip reset");
            tripSwitch.set(TRIP_THRESHOLD);
        }

        matches.keySet().forEach(
                key -> {
                    var info = matches.get(key);
                    var competitionLatch = latch.get(key).get();
                    if(competitionLatch) {
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
    }

    private void tripSwitchActivated() {
        log.info("trip switch activated");

        while (!failedResultsRequests.isEmpty()) {
            var failed = failedResultsRequests.pop();
            log.warn("clearing results: {}, date: {},",
                    failed.getLeft().name().toLowerCase(),
                    failed.getRight().toString());
        }

        while ((!failedPlayersRequests.isEmpty())) {
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
