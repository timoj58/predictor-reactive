package com.timmytime.predictormessagereactive.service.impl;

import com.timmytime.predictormessagereactive.action.EventAction;
import com.timmytime.predictormessagereactive.enumerator.Action;
import com.timmytime.predictormessagereactive.enumerator.Event;
import com.timmytime.predictormessagereactive.enumerator.EventType;
import com.timmytime.predictormessagereactive.facade.WebClientFacade;
import com.timmytime.predictormessagereactive.model.CycleEvent;
import com.timmytime.predictormessagereactive.model.PredictorCycle;
import com.timmytime.predictormessagereactive.repo.PredictorCycleRepo;
import com.timmytime.predictormessagereactive.service.OrchestrationService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class OrchestrationServiceImpl implements OrchestrationService {

    private final List<Event> PLAYER_PREDICTION_EVENTS = Arrays.asList(Event.PLAYERS_TRAINED, Event.EVENTS_LOADED);
    private final List<Event> TEAM_PREDICTION_EVENTS = Arrays.asList(Event.TEAMS_TRAINED, Event.EVENTS_LOADED);

    private final BiFunction<List<CycleEvent>, Consumer<String>, Boolean> training = (ce, c) -> {
        if (ce.stream().filter(f -> f.getMessage().getEvent().equals(Event.DATA_LOADED))
                .map(m -> m.getMessage().getEventType())
                .collect(Collectors.toList())
                .containsAll(EventType.competitions())
        ) {
            EventType.countries().forEach(country -> c.accept(country.name()));
            return true;
        }
        return false;

    };

    private final BiFunction<Pair<List<CycleEvent>, List<Event>>, Consumer<String>, Boolean> predictions = (ce, c) -> {
        if (ce.getLeft().stream().filter(f -> ce.getRight().contains(f.getMessage().getEvent()))
                .map(m -> m.getMessage().getEventType())
                .collect(Collectors.toList())
                .containsAll(EventType.countriesAndAll())
        ) {
            EventType.countries().forEach(country -> c.accept(country.name()));
            return true;
        }
        return false;
    };


    private final List<CycleEvent> events = new ArrayList<>();
    private final Map<Action, EventAction> eventManager = new ConcurrentHashMap<>();

    @Autowired
    public OrchestrationServiceImpl(
            WebClientFacade webClientFacade,
            PredictorCycleRepo predictorCycleRepo
    ) {

        eventManager.put(Action.TRAIN_TEAMS, EventAction.builder()
                .processed(Boolean.FALSE)
                .handler((ce) -> training.apply(ce, (c) -> webClientFacade.train("")))
                .build());

        eventManager.put(Action.TRAIN_PLAYERS, EventAction.builder()
                .processed(Boolean.FALSE)
                .handler((ce) -> training.apply(ce, (c) -> webClientFacade.train("")))
                .build());

        eventManager.put(Action.PREDICT_TEAMS, EventAction.builder()
                .processed(Boolean.FALSE)
                .handler((ce) -> predictions.apply(
                        Pair.of(ce, TEAM_PREDICTION_EVENTS), (c) -> webClientFacade.predict("")))
                .build());

        eventManager.put(Action.PREDICT_PLAYERS, EventAction.builder()
                .processed(Boolean.FALSE)
                .handler((ce) -> predictions.apply(
                        Pair.of(ce, PLAYER_PREDICTION_EVENTS), (c) -> webClientFacade.predict("")))
                .build());

        eventManager.put(Action.SCRAPE, EventAction.builder()
                .processed(Boolean.FALSE)
                .handler((ce) -> {
                    if (ce.stream().anyMatch(m -> m.getMessage().getEvent().equals(Event.START))) {
                        webClientFacade.scrape("");
                        webClientFacade.scrape("");
                        return true;
                    }
                    return false;
                })
                .build());

        eventManager.put(Action.FINISH, EventAction.builder()
                .processed(Boolean.FALSE)
                .handler((ce) -> {
                    if (ce.stream().map(m -> m.getMessage().getEvent()).collect(Collectors.toList()).containsAll(
                            Arrays.asList(Event.PLAYERS_PREDICTED, Event.TEAMS_PREDICTED)
                    )) {
                        webClientFacade.finish("");
                        return true;
                    }
                    return false;
                })
                .build());

        eventManager.put(Action.FINALISE, EventAction.builder()
                .processed(Boolean.FALSE)
                .handler((ce) -> {
                    if (ce.stream().anyMatch(m -> m.getMessage().getEvent().equals(Event.STOP))) {
                        predictorCycleRepo.save(
                                PredictorCycle.builder()
                                        .id(UUID.randomUUID())
                                        .cycleEvents(ce)
                                        .date(LocalDateTime.now())
                                        .build()
                        ).subscribe();
                        return true;
                    }
                    return false;
                }).build());
    }


    @Override
    public void process(CycleEvent cycleEvent) {

        Mono.just(cycleEvent)
                .doOnNext(events::add)
                .doFinally(check ->
                        Flux.just(Action.values())
                                .filter(action -> !eventManager.get(action).getProcessed())
                                .subscribe(action -> {
                                    var actionEvent = eventManager.get(action);
                                    eventManager.get(action).setProcessed(actionEvent.getHandler().apply(events));
                                }))
                .subscribe();


    }

}
