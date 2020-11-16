package com.timmytime.predictoreventsreactive.service.impl;

import com.timmytime.predictoreventsreactive.enumerator.Predictions;
import com.timmytime.predictoreventsreactive.facade.WebClientFacade;
import com.timmytime.predictoreventsreactive.model.Event;
import com.timmytime.predictoreventsreactive.model.EventOutcome;
import com.timmytime.predictoreventsreactive.request.Prediction;
import com.timmytime.predictoreventsreactive.request.TensorflowPrediction;
import com.timmytime.predictoreventsreactive.service.EventOutcomeService;
import com.timmytime.predictoreventsreactive.service.EventService;
import com.timmytime.predictoreventsreactive.service.TensorflowPredictionService;
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

//@Disabled
class PredictionServiceImplTest {

    private final EventService eventService = mock(EventService.class);
    private final WebClientFacade webClientFacade = mock(WebClientFacade.class);
    private final TensorflowPredictionService tensorflowPredictionService = new TensorflowPredictionServiceImpl(
            "", "", "", 0, webClientFacade
    );
    private static final EventOutcomeService eventOutcomeService = mock(EventOutcomeService.class);


    private final PredictionServiceImpl predictionService
            = new PredictionServiceImpl(
                    0,
                    eventService,
            tensorflowPredictionService,
            eventOutcomeService
    );

    @BeforeAll
    public static void setUp(){

        //this will cause a loop in this test obviously....to resolve.
        when(eventOutcomeService.toFix()).thenReturn(
                Flux.fromStream(Arrays.asList(
                        EventOutcome.builder().eventType(Predictions.PREDICT_GOALS.name())
                                .id(UUID.randomUUID())
                                .build()
                ).stream())
        );
    }


    @Test
    public void startTest() throws InterruptedException {

        when(eventService.getEvents(any())).thenReturn(
                Flux.fromStream(Arrays.asList(new Event()).stream())
        );
        when(eventOutcomeService.save(any())).thenReturn(Mono.just(EventOutcome.builder()
                .id(UUID.randomUUID())
                .competition("turkey_1").build()));

        predictionService.start("TURKEY");
        Thread.sleep(4000);

        verify(webClientFacade, atLeastOnce()).predict(anyString(), any());

    }

    private Flux<Integer> receiver;
    private Consumer<Integer> consumer;

    Boolean flag = Boolean.FALSE;

    @Test
    public void sanity() throws InterruptedException {
        this.receiver
                = Flux.push(sink -> consumer = (t) -> sink.next(t), FluxSink.OverflowStrategy.BUFFER);

        this.receiver
                .limitRate(1)
                .doOnNext(this::process)
                .doFinally(end -> replay()
                ).subscribe();


        IntStream.range(0, 50).forEach(i -> consumer.accept(i));

        Thread.sleep(1000);

    }

    private void process(Integer i){
        System.out.println(i);

        if(i>100){flag = Boolean.TRUE;}
    }

    private void replay(){
        System.out.println("replay");
        if(!flag) {
            IntStream.range(100, 200).forEach(i -> consumer.accept(i));
        }
    }

}