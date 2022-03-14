package com.timmytime.predictormessagereactive.service.impl;

import com.timmytime.predictormessagereactive.action.EventManager;
import com.timmytime.predictormessagereactive.enumerator.Action;
import com.timmytime.predictormessagereactive.model.ActionEvent;
import com.timmytime.predictormessagereactive.model.CycleEvent;
import com.timmytime.predictormessagereactive.service.OrchestrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;


@Slf4j
@Service
public class OrchestrationServiceImpl implements OrchestrationService {

    private final List<CycleEvent> events = new ArrayList<>();
    private final List<ActionEvent> actions = new ArrayList<>();
    private final EventManager eventManager;
    private Consumer<CycleEvent> consumer;

    @Autowired
    public OrchestrationServiceImpl(
            EventManager eventManager
    ) {
        this.eventManager = eventManager;
        Flux<CycleEvent> receiver = Flux.create(sink -> consumer = sink::next, FluxSink.OverflowStrategy.BUFFER);
        receiver.limitRate(1).subscribe(this::testCycleEvents);
    }


    @Override
    public void process(CycleEvent cycleEvent) {
        consumer.accept(cycleEvent);
    }

    @Override
    public Mono<Boolean> testStatus(String action) {
        return Mono.just(actions.stream().anyMatch(a -> a.getAction().equals(Action.valueOf(action))));
    }

    private void testCycleEvents(CycleEvent cycleEvent) {
        CompletableFuture.runAsync(() -> events.add(cycleEvent))
                .thenRun(() ->
                        Flux.just(Action.values())
                                .filter(action -> !eventManager.get(action).getProcessed())
                                .subscribe(action -> {
                                    var actionEvent = eventManager.get(action);
                                    var actionResult = actionEvent.getHandler().apply(events, actions);

                                    if (actionResult)
                                        actions.add(ActionEvent.builder()
                                                .action(action)
                                                .timestamp(LocalDateTime.now())
                                                .build());

                                    eventManager.get(action).setProcessed(actionResult);
                                }));
    }

}
