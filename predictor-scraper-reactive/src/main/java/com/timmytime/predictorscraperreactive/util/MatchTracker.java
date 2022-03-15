package com.timmytime.predictorscraperreactive.util;

import com.timmytime.predictorscraperreactive.enumerator.CompetitionFixtureCodes;
import com.timmytime.predictorscraperreactive.enumerator.TrackerQueueAction;
import com.timmytime.predictorscraperreactive.request.Message;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
@Component
public class MatchTracker {
    @Getter
    private final Map<CompetitionFixtureCodes, Pair<AtomicInteger, List<String>>> matches = new HashMap<>();
    private final Map<CompetitionFixtureCodes, AtomicBoolean> latch = new HashMap<>();

    public MatchTracker() {
        Flux.fromArray(CompetitionFixtureCodes.values())
                .subscribe(competition -> {
                    matches.put(competition, Pair.of(new AtomicInteger(0), new ArrayList<>()));
                    latch.put(competition, new AtomicBoolean(Boolean.FALSE));
                });

    }

    public void handle(Triple<CompetitionFixtureCodes, String, TrackerQueueAction> item) {
        switch (item.getRight()) {
            case ADD_MATCH -> matches.get(item.getLeft()).getRight().add(item.getMiddle());
            case REMOVE_MATCH -> matches.get(item.getLeft()).getRight().remove(item.getMiddle());
            case UPDATE_IN_QUEUE -> {
                matches.get(item.getLeft()).getLeft().getAndAdd(Integer.parseInt(item.getMiddle()));
                if (latch.get(item.getLeft()).get() == Boolean.FALSE) {
                    latch.get(item.getLeft()).set(Boolean.TRUE);
                }
            }
            case REMOVE_IN_QUEUE -> matches.get(item.getLeft()).getLeft().decrementAndGet();
        }
    }

    public Triple<Boolean, Integer, Integer> calculate(Supplier<Integer> messagesSent) {
        var messageSentCount = messagesSent.get();
        var latchStatus = getLatchStatus();
        var queueTotal = getQueueTotal();

        log.info("messagesSent: {}, latchStatus: {}, queueTotal: {}", messageSentCount, latchStatus, queueTotal);

        return Triple.of(latchStatus, queueTotal, messageSentCount);
    }

    public void testFinished(Consumer<Message> send) {
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
                            send.accept(new Message(key.name().toLowerCase()));
                        }
                    }
                }
        );

    }

    private boolean getLatchStatus() {
        return latch.values().stream().allMatch(AtomicBoolean::get);
    }

    private int getQueueTotal() {
        return matches.values().stream().map(Pair::getLeft).mapToInt(AtomicInteger::get).sum();
    }
}
