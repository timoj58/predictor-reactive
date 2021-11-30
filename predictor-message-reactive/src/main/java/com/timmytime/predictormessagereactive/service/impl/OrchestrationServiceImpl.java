package com.timmytime.predictormessagereactive.service.impl;

import com.timmytime.predictormessagereactive.action.EventAction;
import com.timmytime.predictormessagereactive.configuration.HostsConfiguration;
import com.timmytime.predictormessagereactive.enumerator.Action;
import com.timmytime.predictormessagereactive.enumerator.Event;
import com.timmytime.predictormessagereactive.enumerator.EventType;
import com.timmytime.predictormessagereactive.facade.WebClientFacade;
import com.timmytime.predictormessagereactive.model.ActionEvent;
import com.timmytime.predictormessagereactive.model.CycleEvent;
import com.timmytime.predictormessagereactive.model.PredictorCycle;
import com.timmytime.predictormessagereactive.repo.PredictorCycleRepo;
import com.timmytime.predictormessagereactive.request.Message;
import com.timmytime.predictormessagereactive.service.InitService;
import com.timmytime.predictormessagereactive.service.OrchestrationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
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

    private final BiFunction<Triple<List<CycleEvent>, List<Event>, List<EventType>>, Pair<Integer, Consumer<EventType>>, Boolean> predictions = (events, predict) -> {
        if (transform.apply(events.getLeft().stream().filter(f -> events.getMiddle().contains(f.getMessage().getEvent())))
                .containsAll(events.getRight())
        ) {
            Flux.fromStream(EventType.countries().stream())
                    .limitRate(1)
                    .delayElements(Duration.ofSeconds(predict.getLeft()))
                    .subscribe(c -> predict.getRight().accept(c));
            return true;
        }
        return false;
    };

    private final List<CycleEvent> events = new ArrayList<>();
    private final List<ActionEvent> actions = new ArrayList<>();
    private final Map<Action, EventAction> eventManager = new ConcurrentHashMap<>();

    @Autowired
    public OrchestrationServiceImpl(
            @Value("${test.delay}") Integer delay,
            WebClientFacade webClientFacade,
            PredictorCycleRepo predictorCycleRepo,
            InitService initService,
            HostsConfiguration hostsConfiguration
    ) {
        eventManager.put(Action.TRAIN_TEAMS, EventAction.builder()
                .processed(Boolean.FALSE)
                .handler((ce) -> training.apply(
                        ce,
                        () -> Flux.fromStream(EventType.countries().stream())
                                .limitRate(1)
                                .delayElements(Duration.ofSeconds(delay))
                                .subscribe(country ->
                                        webClientFacade.train(hostsConfiguration.getTeams() + "/message",
                                                Message.builder()
                                                        .event(Event.TEAMS_TRAINED)
                                                        .eventType(country)
                                                        .build()))))
                .build());

        eventManager.put(Action.TRAIN_PLAYERS, EventAction.builder()
                .processed(Boolean.FALSE)
                //input data loaded for all competitions, output -> train all
                .handler((ce) -> training.apply(
                        ce,
                        () -> webClientFacade.train(hostsConfiguration.getPlayers() + "/message",
                                Message.builder()
                                        .event(Event.PLAYERS_TRAINED)
                                        .eventType(EventType.ALL)
                                        .build()
                        )))
                .build());

        eventManager.put(Action.PREDICT_TEAMS, EventAction.builder()
                .processed(Boolean.FALSE)
                //input all countries trained, countries
                .handler((ce) -> predictions.apply(
                        Triple.of(ce, TEAM_PREDICTION_EVENTS, EventType.countriesAndCompetitions()),
                        Pair.of(delay, (c) -> webClientFacade.predict(
                                hostsConfiguration.getTeamEvents() + "/message",
                                Message.builder()
                                        .event(Event.TEAMS_PREDICTED)
                                        .eventType(c)
                                        .build()
                        ))))
                .build());

        eventManager.put(Action.PREDICT_PLAYERS, EventAction.builder()
                .processed(Boolean.FALSE)
                .handler((ce) -> predictions.apply(
                        Triple.of(ce, PLAYER_PREDICTION_EVENTS, EventType.competitionsAndAll()),
                        Pair.of(delay, (c) -> webClientFacade.predict(
                                hostsConfiguration.getPlayerEvents() + "/message",
                                Message.builder()
                                        .event(Event.PLAYERS_PREDICTED)
                                        .eventType(c)
                                        .build()
                        ))))
                .build());

        eventManager.put(Action.SCRAPE, EventAction.builder()
                .processed(Boolean.FALSE)
                .handler((ce) -> {
                    if (ce.stream().anyMatch(m -> m.getMessage().getEvent().equals(Event.START))) {
                        initService.init().doFinally(scrape ->
                                        CompletableFuture.runAsync(() -> webClientFacade.scrape(hostsConfiguration.getDataScraper() + "/scrape"))
                                                .thenRun(() -> webClientFacade.scrape(hostsConfiguration.getEventsScraper() + "/scrape")))
                                .subscribe();
                        return true;
                    }
                    return false;
                })
                .build());

        eventManager.put(Action.STOP_PLAYERS_MACHINE, EventAction.builder()
                .processed(Boolean.FALSE)
                .handler((ce) -> {
                    if (ce.stream().map(m -> m.getMessage().getEvent()).collect(Collectors.toList()).contains(Event.PLAYERS_PREDICTED)) {
                        webClientFacade.finish(
                                hostsConfiguration.getClient() + "/message",
                                Message.builder()
                                        .event(Event.PLAYERS_PREDICTED)
                                        .eventType(EventType.ALL)
                                        .build()
                        );
                        return true;
                    }
                    return false;
                })
                .build());

        eventManager.put(Action.STOP_TEAM_MACHINE, EventAction.builder()
                .processed(Boolean.FALSE)
                .handler((ce) -> {
                    if (ce.stream().map(m -> m.getMessage().getEvent()).collect(Collectors.toList()).contains(Event.TEAMS_PREDICTED)) {
                        webClientFacade.finish(
                                hostsConfiguration.getClient() + "/message",
                                Message.builder()
                                        .event(Event.TEAMS_PREDICTED)
                                        .eventType(EventType.ALL)
                                        .build()
                        );
                        return true;
                    }
                    return false;
                })
                .build());

        eventManager.put(Action.FINALISE, EventAction.builder()
                .processed(Boolean.FALSE)
                .handler((ce) -> {
                    if (ce.stream().anyMatch(m -> m.getMessage().getEvent().equals(Event.STOP))) {
                        actions.add(ActionEvent.builder()
                                .action(Action.FINALISE)
                                .timestamp(LocalDateTime.now())
                                .build());

                        predictorCycleRepo.save(
                                PredictorCycle.builder()
                                        .id(UUID.randomUUID())
                                        .cycleEvents(ce)
                                        .date(LocalDateTime.now())
                                        .actionEvents(actions)
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

    @Override
    public Mono<Boolean> testStatus(String action) {
        return Mono.just(actions.stream().anyMatch(a -> a.getAction().equals(Action.valueOf(action))));
    }

}
