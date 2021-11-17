package com.timmytime.predictoreventsreactive.e2e;

import com.timmytime.predictoreventsreactive.enumerator.Messages;
import com.timmytime.predictoreventsreactive.facade.WebClientFacade;
import com.timmytime.predictoreventsreactive.model.Event;
import com.timmytime.predictoreventsreactive.model.EventOutcome;
import com.timmytime.predictoreventsreactive.repo.EventOutcomeRepo;
import com.timmytime.predictoreventsreactive.request.Message;
import com.timmytime.predictoreventsreactive.service.*;
import com.timmytime.predictoreventsreactive.service.impl.*;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

public class EventsServiceTest {

    private final WebClientFacade webClientFacade = mock(WebClientFacade.class);
    private final EventOutcomeRepo eventOutcomeRepo = mock(EventOutcomeRepo.class);

    private final EventService eventService
            = new EventServiceImpl("event-data", webClientFacade);
    private final TensorflowPredictionService tensorflowPredictionService
            = new TensorflowPredictionServiceImpl("training", "results", "goals", 0, webClientFacade);
    private final EventOutcomeService eventOutcomeService
            = new EventOutcomeServiceImpl(eventOutcomeRepo);


    private final PredictionService predictionService =
            new PredictionServiceImpl(0, eventService, tensorflowPredictionService, eventOutcomeService);
    private final PredictionResultService predictionResultService
            = new PredictionResultServiceImpl(eventOutcomeService);
    private final ValidationService validationService
            = new ValidationServiceImpl("data", eventOutcomeService, webClientFacade);
    private final PredictionMonitorService predictionMonitorService
            = new PredictionMonitorService("clients", predictionService, webClientFacade);


    private final MessageReceivedService messageReceivedService
            = new MessageReceivedServiceImpl("players", 0, predictionService,
            predictionResultService, predictionMonitorService, validationService, webClientFacade);

    @Test
    void predict() throws InterruptedException {

        var outcome = EventOutcome.builder()
                .id(UUID.randomUUID())
                .competition("greece_1").build();

        when(eventOutcomeRepo.findByCompetitionInAndPreviousEventTrue(any()))
                .thenReturn(Flux.just(outcome));

        when(eventOutcomeRepo.save(any())).thenReturn(Mono.just(outcome));
        when(eventOutcomeRepo.findByCompetitionInAndSuccessNull(any())).thenReturn(Flux.empty());

        when(webClientFacade.getEvents(anyString())).thenReturn(
                Flux.just(Event.builder().build())
        );

        Stream.of(Messages.values()).forEach(msg ->
                messageReceivedService.receive(
                        Mono.just(
                                Message.builder()
                                        .type(msg)
                                        .country("GREECE")
                                        .build()
                        )
                ).subscribe()
        );

        Thread.sleep(250);


        verify(webClientFacade, atLeastOnce()).predict(anyString(), any());
    }
}
