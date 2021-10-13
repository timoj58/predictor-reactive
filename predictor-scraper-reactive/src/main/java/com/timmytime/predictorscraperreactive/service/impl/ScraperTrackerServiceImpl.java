package com.timmytime.predictorscraperreactive.service.impl;


import com.timmytime.predictorscraperreactive.enumerator.CompetitionFixtureCodes;
import com.timmytime.predictorscraperreactive.enumerator.TrackerQueueAction;
import com.timmytime.predictorscraperreactive.service.ScraperTrackerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Slf4j
@Service
public class ScraperTrackerServiceImpl implements ScraperTrackerService {

    private final Deque<Triple<CompetitionFixtureCodes, String, LocalDate>> failedResultsRequests = new ArrayDeque();
    private final Deque<Pair<CompetitionFixtureCodes, Integer>> failedPlayersRequests = new ArrayDeque();
    private final Map<CompetitionFixtureCodes, Pair<AtomicInteger, List<Integer>>> matches = new HashMap<>();

    private Consumer<Triple<CompetitionFixtureCodes, Integer, TrackerQueueAction>> matchTracker;

    @Override
    public void addFailedResultsRequest(Pair<CompetitionFixtureCodes, String> competition, LocalDate date) {
        failedResultsRequests.add(Triple.of(competition.getLeft(), competition.getRight(), date));
    }

    @Override
    public void addFailedPlayersRequest(Pair<CompetitionFixtureCodes, Integer> matchId) {
        failedPlayersRequests.add(matchId);
    }

    @Override
    public List<Triple<CompetitionFixtureCodes, String, LocalDate>> getFailedResultsRequests() {
        log.info("failed results count {}", failedResultsRequests.size());
        List<Triple<CompetitionFixtureCodes, String, LocalDate>> popped = new ArrayList<>();

        while (failedResultsRequests.size() > 0) {
            popped.add(failedResultsRequests.pop());
        }

        return popped;
    }

    @Override
    public List<Pair<CompetitionFixtureCodes, Integer>> getFailedPlayersRequests() {
        log.info("failed player count {}", failedPlayersRequests.size());
        List<Pair<CompetitionFixtureCodes, Integer>> popped = new ArrayList<>();

        while (failedPlayersRequests.size() > 0) {
            popped.add(failedPlayersRequests.pop());
        }
        return popped;
    }

    @Override
    public void addMatchesInQueue(CompetitionFixtureCodes competition) {
        matchTracker.accept(Triple.of(competition, null, TrackerQueueAction.UPDATE_IN_QUEUE));
    }

    @Override
    public void addMatches(CompetitionFixtureCodes competition, List<Integer> matchIds) {
        matchIds.forEach(id -> matchTracker.accept(Triple.of(competition, id, TrackerQueueAction.ADD_MATCH)));
    }

    @Override
    public void removeMatch(Pair<CompetitionFixtureCodes, Integer> matchId) {
        matchTracker.accept(Triple.of(matchId.getLeft(), matchId.getRight(), TrackerQueueAction.REMOVE_MATCH));
    }


    @PostConstruct
    private void init() {
        Flux.fromArray(CompetitionFixtureCodes.values())
                .subscribe(competition -> matches.put(competition, Pair.of(new AtomicInteger(0), new ArrayList<>())));

        Flux<Triple<CompetitionFixtureCodes, Integer, TrackerQueueAction>> trackerQueue = Flux.push(sink ->
                ScraperTrackerServiceImpl.this.matchTracker = sink::next, FluxSink.OverflowStrategy.BUFFER);

        trackerQueue.limitRate(1).subscribe(this::trackerQueueHandler);
    }

    private void trackerQueueHandler(Triple<CompetitionFixtureCodes, Integer, TrackerQueueAction> item) {
        switch (item.getRight()) {
            case ADD_MATCH:
                matches.get(item.getLeft()).getRight().add(item.getMiddle());
                break;
            case REMOVE_MATCH:
                matches.get(item.getLeft()).getRight().remove(item.getMiddle());
                break;
            case UPDATE_IN_QUEUE:
                matches.get(item.getLeft()).getLeft().incrementAndGet();
                break;
        }
    }

    @Scheduled(fixedRate = 48000)
    private void tracker() {
        log.info("checking status");
        //for now just log.  in queue value useful.. how to use it?  but progress..
        matches.keySet().forEach(
                key -> log.info("we have: in queue: {}, outstanding {}  for {}",
                        matches.get(key).getLeft().get(), matches.get(key).getRight().size(), key.name().toLowerCase())
        );
    }

}
