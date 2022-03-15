package com.timmytime.predictormessagereactive.service.impl;

import com.timmytime.predictormessagereactive.action.EventManager;
import com.timmytime.predictormessagereactive.action.event.*;
import com.timmytime.predictormessagereactive.configuration.HostsConfiguration;
import com.timmytime.predictormessagereactive.enumerator.Event;
import com.timmytime.predictormessagereactive.enumerator.EventType;
import com.timmytime.predictormessagereactive.facade.WebClientFacade;
import com.timmytime.predictormessagereactive.model.CycleEvent;
import com.timmytime.predictormessagereactive.repo.PredictorCycleRepo;
import com.timmytime.predictormessagereactive.request.Message;
import com.timmytime.predictormessagereactive.service.InitService;
import com.timmytime.predictormessagereactive.service.OrchestrationService;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

class OrchestrationServiceImplTest {

    private static final long WAIT = 250;

    private final WebClientFacade webClientFacade = mock(WebClientFacade.class);
    private final PredictorCycleRepo predictorCycleRepo = mock(PredictorCycleRepo.class);
    private final InitService initService = mock(InitService.class);
    private final HostsConfiguration hostsConfiguration = mock(HostsConfiguration.class);

    private final EventManager eventManager
            = new EventManager(
            List.of(
                    new Scrape(0, webClientFacade,hostsConfiguration, initService),
                    new Finalise(predictorCycleRepo),
                    new StopPlayersMachine(webClientFacade, hostsConfiguration),
                    new StopTeamsMachine(webClientFacade, hostsConfiguration),
                    new TrainPlayers(webClientFacade, hostsConfiguration),
                    new TrainTeams(0, webClientFacade, hostsConfiguration),
                    new PredictTeams(0, webClientFacade, hostsConfiguration),
                    new PredictPlayers(0,webClientFacade, hostsConfiguration)
            )
    );

    private final OrchestrationService orchestrationService
            = new OrchestrationServiceImpl(eventManager);

    @Test
    void stop() throws InterruptedException {

        when(predictorCycleRepo.save(any())).thenReturn(Mono.empty());

        orchestrationService.process(
                CycleEvent.builder()
                        .message(
                                Message.builder()
                                        .event(Event.STOP)
                                        .eventType(EventType.ALL)
                                        .build()
                        ).build()
        );

        Thread.sleep(WAIT);

        verify(predictorCycleRepo, atLeastOnce()).save(any());

    }

    @Test
    void start() throws InterruptedException {

        when(initService.init()).thenReturn(Flux.just("1"));

        orchestrationService.process(
                CycleEvent.builder()
                        .message(
                                Message.builder()
                                        .event(Event.START)
                                        .eventType(EventType.ALL)
                                        .build()
                        ).build()
        );

        Thread.sleep(WAIT);

        verify(webClientFacade, atLeast(2)).scrape(anyString());

    }

    @Test
    void finish() throws InterruptedException {

        Arrays.asList(Event.PLAYERS_PREDICTED, Event.TEAMS_PREDICTED)
                .forEach(event -> orchestrationService.process(
                        CycleEvent.builder()
                                .message(
                                        Message.builder()
                                                .event(event)
                                                .eventType(EventType.ALL)
                                                .build()
                                ).build()
                ));

        Thread.sleep(WAIT);

        verify(webClientFacade, atLeastOnce()).finish(anyString(), any());

    }

    @Test
    void trainPlayers() throws InterruptedException {
        EventType.competitions()
                .forEach(competition -> orchestrationService.process(
                        CycleEvent.builder()
                                .message(
                                        Message.builder()
                                                .event(Event.DATA_LOADED)
                                                .eventType(competition)
                                                .build()
                                ).build()
                ));

        Thread.sleep(WAIT);

        verify(webClientFacade, atLeast(EventType.countries().size())).train(anyString(), any());

    }

    @Test
    void predictPlayers() throws InterruptedException {
        EventType.competitions()
                .forEach(competition -> orchestrationService.process(
                        CycleEvent.builder()
                                .message(
                                        Message.builder()
                                                .event(Event.EVENTS_LOADED)
                                                .eventType(competition)
                                                .build()
                                ).build()
                ));

        verify(webClientFacade, never()).predict(anyString(), any());

        orchestrationService.process(
                CycleEvent.builder()
                        .message(
                                Message.builder()
                                        .event(Event.PLAYERS_TRAINED)
                                        .eventType(EventType.ALL)
                                        .build()
                        ).build()
        );

        Thread.sleep(WAIT);

        verify(webClientFacade, atLeast(EventType.countries().size())).predict(anyString(), any());
    }

    @Test
    void trainTeams() throws InterruptedException {

        EventType.competitions()
                .forEach(competition -> orchestrationService.process(
                        CycleEvent.builder()
                                .message(
                                        Message.builder()
                                                .event(Event.DATA_LOADED)
                                                .eventType(competition)
                                                .build()
                                ).build()
                ));

        Thread.sleep(WAIT);

        verify(webClientFacade, atLeast(EventType.countries().size())).train(anyString(), any());

    }

    @Test
    void predictTeams() throws InterruptedException {
        EventType.countries()
                .forEach(country -> orchestrationService.process(
                        CycleEvent.builder()
                                .message(
                                        Message.builder()
                                                .event(Event.TEAMS_TRAINED)
                                                .eventType(country)
                                                .build()
                                ).build()
                ));

        verify(webClientFacade, never()).predict(anyString(), any());

        EventType.competitions()
                .forEach(competition ->
                        orchestrationService.process(
                                CycleEvent.builder()
                                        .message(
                                                Message.builder()
                                                        .event(Event.EVENTS_LOADED)
                                                        .eventType(competition)
                                                        .build()
                                        ).build()
                        ));

        Thread.sleep(WAIT);

        verify(webClientFacade, atLeast(EventType.countries().size())).predict(anyString(), any());

    }

}