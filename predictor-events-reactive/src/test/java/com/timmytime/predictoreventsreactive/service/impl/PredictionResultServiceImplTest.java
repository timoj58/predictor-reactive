package com.timmytime.predictoreventsreactive.service.impl;

import com.timmytime.predictoreventsreactive.model.EventOutcome;
import com.timmytime.predictoreventsreactive.service.EventOutcomeService;
import com.timmytime.predictoreventsreactive.service.PredictionResultService;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PredictionResultServiceImplTest {

    private final EventOutcomeService eventOutcomeService = mock(EventOutcomeService.class);

    private final PredictionResultService predictionResultService
            = new PredictionResultServiceImpl(eventOutcomeService);

    @Test
    void result() throws InterruptedException {

        when(eventOutcomeService.find(any())).thenReturn(
                Mono.just(EventOutcome.builder()
                        .prediction("{\"result\":[{\"score\":83.4,\"key\":\"0\"},{\"score\":13.9,\"key\":\"1\"},{\"score\":2.1,\"key\":\"6\"},{\"score\":0.4,\"key\":\"5\"},{\"score\":0.2,\"key\":\"2\"},{\"score\":0,\"key\":\"11\"},{\"score\":0,\"key\":\"12\"},{\"score\":0,\"key\":\"0\"},{\"score\":0,\"key\":\"3\"},{\"score\":0,\"key\":\"7\"},{\"score\":0,\"key\":\"8\"},{\"score\":0,\"key\":\"9\"},{\"score\":0,\"key\":\"10\"}],\"away\":\"Peterhead\",\"type\":\"PREDICT_GOALS\",\"home\":\"Edinburgh City\"}")
                        .build())
        );
        when(eventOutcomeService.save(any())).thenReturn(Mono.empty());

        predictionResultService.result(UUID.randomUUID(), new JSONObject());

        Thread.sleep(100);

        verify(eventOutcomeService, atLeastOnce()).save(any());
    }
}