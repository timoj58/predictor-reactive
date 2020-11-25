package com.timmytime.predictoreventsreactive.service.impl;

import com.timmytime.predictoreventsreactive.cache.ReceiptCache;
import com.timmytime.predictoreventsreactive.enumerator.Predictions;
import com.timmytime.predictoreventsreactive.facade.WebClientFacade;
import com.timmytime.predictoreventsreactive.model.Event;
import com.timmytime.predictoreventsreactive.model.EventOutcome;
import com.timmytime.predictoreventsreactive.request.Prediction;
import com.timmytime.predictoreventsreactive.request.TensorflowPrediction;
import com.timmytime.predictoreventsreactive.service.EventOutcomeService;
import com.timmytime.predictoreventsreactive.service.EventService;
import com.timmytime.predictoreventsreactive.service.TensorflowPredictionService;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Disabled
class PredictionServiceImplTest {

    private final EventService eventService = mock(EventService.class);
    private final WebClientFacade webClientFacade = mock(WebClientFacade.class);
    private static final ReceiptCache receiptCache = mock(ReceiptCache.class);
    private final TensorflowPredictionService tensorflowPredictionService = new TensorflowPredictionServiceImpl(
            "", "", "", 0, webClientFacade, receiptCache
    );
    private static final EventOutcomeService eventOutcomeService = mock(EventOutcomeService.class);


    private final PredictionServiceImpl predictionService
            = new PredictionServiceImpl(
                    0,
                    eventService,
            tensorflowPredictionService,
            eventOutcomeService,
            receiptCache
    );

    private static final UUID event1 = UUID.randomUUID();
    private static final UUID event2 = UUID.randomUUID();
    private static final UUID event3 = UUID.randomUUID();
    private static final UUID replay1 = UUID.randomUUID();


    @BeforeAll
    public static void setUp(){

        when(receiptCache.isEmpty(event1)).thenReturn(Boolean.FALSE);
        when(receiptCache.isEmpty(event2)).thenReturn(Boolean.FALSE);
        when(receiptCache.isEmpty(event3)).thenReturn(Boolean.TRUE);
        when(receiptCache.isEmpty(replay1)).thenReturn(Boolean.FALSE);


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

        //now fire some results back...to test the mechanism on finish.
        predictionService.result(event1, new JSONObject());
        predictionService.result(event2, new JSONObject());
        predictionService.result(event3, new JSONObject());


        //not mow....

        //sleep again.

        verify(webClientFacade, atLeastOnce()).predict(anyString(), any());

    }

}