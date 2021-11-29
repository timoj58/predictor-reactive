package com.timmytime.predictormessagereactive.service.impl;

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

import static org.mockito.Mockito.*;

class OrchestrationServiceImplTest {

    private final WebClientFacade webClientFacade = mock(WebClientFacade.class);
    private final PredictorCycleRepo predictorCycleRepo = mock(PredictorCycleRepo.class);
    private final InitService initService = mock(InitService.class);
    private final HostsConfiguration hostsConfiguration = mock(HostsConfiguration.class);

    private final OrchestrationService orchestrationService
            = new OrchestrationServiceImpl(webClientFacade, predictorCycleRepo, initService, hostsConfiguration);

    @Test
    void stop() {

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

        Thread.sleep(100);

        verify(webClientFacade, atLeast(2)).scrape(anyString());

    }

    @Test
    void finish() {

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

        verify(webClientFacade, atLeastOnce()).finish(anyString(), any());

    }

    @Test
    void trainPlayers() {
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

        verify(webClientFacade, atLeast(EventType.countries().size())).train(anyString(), any());

    }

    @Test
    void predictPlayers() {
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

        verify(webClientFacade, atLeast(EventType.countries().size())).predict(anyString(), any());
    }

    @Test
    void trainTeams() {

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

        verify(webClientFacade, atLeast(EventType.countries().size())).train(anyString(), any());

    }

    @Test
    void predictTeams() {
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

        verify(webClientFacade, atLeast(EventType.countries().size())).predict(anyString(), any());

    }

}