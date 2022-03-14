package com.timmytime.predictormessagereactive.action.handler;

import com.timmytime.predictormessagereactive.enumerator.Event;
import com.timmytime.predictormessagereactive.enumerator.EventType;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

public class PredictionHandler {
    public Boolean predictions(
            Triple<List<EventType>, List<Event>, List<EventType>> events,
            Pair<Integer, Consumer<EventType>> predict) {
        if (events.getLeft().containsAll(events.getRight())
        ) {
            Flux.fromStream(EventType.countries().stream())
                    .limitRate(1)
                    .delayElements(Duration.ofSeconds(predict.getLeft()))
                    .subscribe(c -> predict.getRight().accept(c));
            return true;
        }
        return false;
    }

}
