package com.timmytime.predictormessagereactive.service.impl;

import com.timmytime.predictormessagereactive.action.EventAction;
import com.timmytime.predictormessagereactive.enumerator.Action;
import com.timmytime.predictormessagereactive.enumerator.Event;
import com.timmytime.predictormessagereactive.enumerator.EventType;
import com.timmytime.predictormessagereactive.facade.LambdaFacade;
import com.timmytime.predictormessagereactive.facade.WebClientFacade;
import com.timmytime.predictormessagereactive.model.ActionEvent;
import com.timmytime.predictormessagereactive.model.CycleEvent;
import com.timmytime.predictormessagereactive.model.PredictorCycle;
import com.timmytime.predictormessagereactive.repo.PredictorCycleRepo;
import com.timmytime.predictormessagereactive.service.OrchestrationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class OrchestrationServiceImpl implements OrchestrationService {

    private final List<Event> PLAYER_PREDICTION_EVENTS = Arrays.asList(Event.PLAYERS_TRAINED, Event.EVENTS_LOADED);
    private final List<Event> TEAM_PREDICTION_EVENTS = Arrays.asList(Event.TEAMS_TRAINED, Event.EVENTS_LOADED);

    private final Function<Stream<CycleEvent>, List<EventType>> transform = (events) ->
            events.map(m -> m.getMessage().getEventType()).collect(Collectors.toList());

    private final BiFunction<List<CycleEvent>, Runnable, Boolean> training = (events, train) -> {
        if (transform.apply(events.stream().filter(f -> f.getMessage().getEvent().equals(Event.DATA_LOADED)))
                .containsAll(EventType.competitions())
        ) {
            train.run();
            return true;
        }
        return false;

    };

    private final BiFunction<Triple<List<CycleEvent>, List<Event>, List<EventType>>, Consumer<String>, Boolean> predictions = (events, predict) -> {
        if (transform.apply(events.getLeft().stream().filter(f -> events.getMiddle().contains(f.getMessage().getEvent())))
                .containsAll(events.getRight())
        ) {
            EventType.countries().forEach(country -> predict.accept(country.name()));
            return true;
        }
        return false;
    };

    private final List<CycleEvent> events = new ArrayList<>();
    private final List<ActionEvent> actions = new ArrayList<>();
    private final Map<Action, EventAction> eventManager = new ConcurrentHashMap<>();

    @Autowired
    public OrchestrationServiceImpl(
            WebClientFacade webClientFacade,
            LambdaFacade lambdaFacade,
            PredictorCycleRepo predictorCycleRepo
    ) {

        eventManager.put(Action.TRAIN_TEAMS, EventAction.builder()
                .processed(Boolean.FALSE)
                .handler((ce) -> training.apply(
                        ce,
                        () -> EventType.countries().forEach(country -> webClientFacade.train(""))))
                .build());

        eventManager.put(Action.TRAIN_PLAYERS, EventAction.builder()
                .processed(Boolean.FALSE)
                //input data loaded for all competitions, output -> train all
                .handler((ce) -> training.apply(
                        ce,
                        () -> webClientFacade.train("")))
                .build());

        eventManager.put(Action.PREDICT_TEAMS, EventAction.builder()
                .processed(Boolean.FALSE)
                //input all countries trained, countries
                .handler((ce) -> predictions.apply(
                        Triple.of(ce, TEAM_PREDICTION_EVENTS, EventType.countriesAndCompetitions()), (c) -> webClientFacade.predict("")))
                .build());

        eventManager.put(Action.PREDICT_PLAYERS, EventAction.builder()
                .processed(Boolean.FALSE)
                .handler((ce) -> predictions.apply(
                        Triple.of(ce, PLAYER_PREDICTION_EVENTS, EventType.competitionsAndAll()), (c) -> webClientFacade.predict("")))
                .build());

        eventManager.put(Action.SCRAPE, EventAction.builder()
                .processed(Boolean.FALSE)
                .handler((ce) -> {
                    if (ce.stream().anyMatch(m -> m.getMessage().getEvent().equals(Event.START))) {
                        CompletableFuture.runAsync(() -> webClientFacade.scrape(""))
                                .thenRun(() -> webClientFacade.scrape(""));
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
                                        .actionEvents(actions)
                                        .build()
                        ).subscribe(result -> lambdaFacade.shutdown());
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
                                    var actionResult = actionEvent.getHandler().apply(events);
                                    if (actionResult)
                                        actions.add(ActionEvent.builder()
                                                .action(action)
                                                .timestamp(LocalDateTime.now())
                                                .build());

                                    eventManager.get(action).setProcessed(actionResult);
                                }))
                .subscribe();
    }

}
