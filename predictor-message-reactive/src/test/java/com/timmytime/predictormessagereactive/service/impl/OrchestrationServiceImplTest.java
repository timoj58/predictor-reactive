package com.timmytime.predictormessagereactive.service.impl;

import com.timmytime.predictormessagereactive.enumerator.Event;
import com.timmytime.predictormessagereactive.enumerator.EventType;
import com.timmytime.predictormessagereactive.facade.WebClientFacade;
import com.timmytime.predictormessagereactive.model.CycleEvent;
import com.timmytime.predictormessagereactive.repo.PredictorCycleRepo;
import com.timmytime.predictormessagereactive.request.Message;
import com.timmytime.predictormessagereactive.service.OrchestrationService;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrchestrationServiceImplTest {

    private final WebClientFacade webClientFacade = mock(WebClientFacade.class);
    private final PredictorCycleRepo predictorCycleRepo = mock(PredictorCycleRepo.class);

    private final OrchestrationService orchestrationService
            = new OrchestrationServiceImpl(webClientFacade, predictorCycleRepo);

    @Test
    void stop(){

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
    void finish(){

        Arrays.asList(Event.PLAYERS_PREDICTED, Event.TEAMS_PREDICTED)
                .forEach(event ->   orchestrationService.process(
                        CycleEvent.builder()
                                .message(
                                        Message.builder()
                                                .event(event)
                                                .eventType(EventType.ALL)
                                                .build()
                                ).build()
                ));

        verify(webClientFacade, atLeastOnce()).finish(anyString());

    }

    @Test
    void trainPlayers(){
        EventType.competitions()
                .forEach(competition ->   orchestrationService.process(
                        CycleEvent.builder()
                                .message(
                                        Message.builder()
                                                .event(Event.DATA_LOADED)
                                                .eventType(competition)
                                                .build()
                                ).build()
                ));

        verify(webClientFacade, atLeast(EventType.countries().size())).train(anyString());

    }

    @Test
    void predictPlayers(){
        EventType.countries()
                .forEach(country ->   orchestrationService.process(
                        CycleEvent.builder()
                                .message(
                                        Message.builder()
                                                .event(Event.PLAYERS_TRAINED)
                                                .eventType(country)
                                                .build()
                                ).build()
                ));

        verify(webClientFacade, never()).predict(anyString());

        orchestrationService.process(
                CycleEvent.builder()
                        .message(
                                Message.builder()
                                        .event(Event.EVENTS_LOADED)
                                        .eventType(EventType.ALL)
                                        .build()
                        ).build()
        );

        verify(webClientFacade, atLeast(EventType.countries().size())).predict(anyString());
    }

    @Test
    void trainTeams(){
        EventType.competitions()
                .forEach(competition ->   orchestrationService.process(
                        CycleEvent.builder()
                                .message(
                                        Message.builder()
                                                .event(Event.DATA_LOADED)
                                                .eventType(competition)
                                                .build()
                                ).build()
                ));

        verify(webClientFacade, atLeast(EventType.countries().size())).train(anyString());

    }

    @Test
    void predictTeams(){
        EventType.countries()
                .forEach(country ->   orchestrationService.process(
                        CycleEvent.builder()
                                .message(
                                        Message.builder()
                                                .event(Event.TEAMS_TRAINED)
                                                .eventType(country)
                                                .build()
                                ).build()
                ));

        verify(webClientFacade, never()).predict(anyString());

        orchestrationService.process(
                CycleEvent.builder()
                        .message(
                                Message.builder()
                                        .event(Event.EVENTS_LOADED)
                                        .eventType(EventType.ALL)
                                        .build()
                        ).build()
        );

        verify(webClientFacade, atLeast(EventType.countries().size())).predict(anyString());

    }

}