package com.timmytime.predictoreventsreactive.service.impl;

import com.timmytime.predictoreventsreactive.enumerator.Predictions;
import com.timmytime.predictoreventsreactive.facade.WebClientFacade;
import com.timmytime.predictoreventsreactive.model.Event;
import com.timmytime.predictoreventsreactive.model.EventOutcome;
import com.timmytime.predictoreventsreactive.service.EventOutcomeService;
import com.timmytime.predictoreventsreactive.service.EventService;
import com.timmytime.predictoreventsreactive.service.TensorflowPredictionService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.Mockito.*;

@Disabled
class PredictionServiceImplTest {

    private static final EventOutcomeService eventOutcomeService = mock(EventOutcomeService.class);
    private static final UUID event1 = UUID.randomUUID();
    private static final UUID event2 = UUID.randomUUID();
    private static final UUID event3 = UUID.randomUUID();
    private static final UUID replay1 = UUID.randomUUID();
    private final EventService eventService = mock(EventService.class);
    private final WebClientFacade webClientFacade = mock(WebClientFacade.class);
    private final TensorflowPredictionService tensorflowPredictionService = new TensorflowPredictionServiceImpl(
            "", "", "", 0, webClientFacade
    );
    private final PredictionServiceImpl predictionService
            = new PredictionServiceImpl(
            eventService,
            tensorflowPredictionService,
            eventOutcomeService);

    @BeforeAll
    public static void setUp() {


        //this will cause a loop in this test obviously....to resolve.
        when(eventOutcomeService.toFix()).thenReturn(
                Flux.fromStream(Arrays.asList(
                        EventOutcome.builder().eventType(Predictions.PREDICT_GOALS.name())
                                .id(replay1)
                                .build()
                ).stream())
        );
    }


    @Test
    public void startTest() throws InterruptedException {

        when(eventService.getEvents(any())).thenReturn(
                Flux.fromStream(Arrays.asList(new Event()).stream())
        );
        //issue we run two at a time.  this could actually be the issue.
        when(eventOutcomeService.save(any())).thenReturn(Mono.just(EventOutcome.builder()
                .id(event1)
                .competition("turkey_1").build()));

        predictionService.start("TURKEY");
        Thread.sleep(4000);


        //not mow....

        //sleep again.

        verify(webClientFacade, atLeastOnce()).predict(anyString(), any());

    }

}