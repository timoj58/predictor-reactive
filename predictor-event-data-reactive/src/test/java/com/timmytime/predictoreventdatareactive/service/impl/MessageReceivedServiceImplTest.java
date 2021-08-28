package com.timmytime.predictoreventdatareactive.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictoreventdatareactive.enumerator.Providers;
import com.timmytime.predictoreventdatareactive.service.ProviderService;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import static org.mockito.Mockito.*;

class MessageReceivedServiceImplTest {

    private final ProviderService providerService = mock(ProviderService.class);


    private final MessageReceivedServiceImpl messageReceivedService
            = new MessageReceivedServiceImpl(
            providerService
    );

    @Test
    public void messageTest() throws JSONException, JsonProcessingException, InterruptedException {

        messageReceivedService.receive(
                Mono.just(
                        new ObjectMapper().readTree(
                                new JSONObject().put("provider", Providers.ESPN_ODDS.name()).toString()
                        )
                )
        ).subscribe();

        Thread.sleep(2000L);

        verify(providerService, atLeastOnce()).receive(any());


    }

}