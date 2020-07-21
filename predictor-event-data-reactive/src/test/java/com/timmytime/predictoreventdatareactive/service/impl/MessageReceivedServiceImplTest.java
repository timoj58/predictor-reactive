package com.timmytime.predictoreventdatareactive.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictoreventdatareactive.enumerator.Providers;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageReceivedServiceImplTest {

    private final PaddyPowerService paddyPowerService = mock(PaddyPowerService.class);
    private final BetwayService betwayService = mock(BetwayService.class);


    private final MessageReceivedServiceImpl messageReceivedService
            = new MessageReceivedServiceImpl(
                    betwayService, paddyPowerService
    );

    @Test
    public void messageTest() throws JSONException, JsonProcessingException, InterruptedException {

        messageReceivedService.receive(
                Mono.just(
                        new ObjectMapper().readTree(
                                new JSONObject().put("provider", Providers.BETWAY_ODDS.name()).toString()
                        )
                )
        ).subscribe();

        messageReceivedService.receive(
                Mono.just(
                        new ObjectMapper().readTree(
                                new JSONObject().put("provider", Providers.PADDYPOWER_ODDS.name()).toString()
                        )
                )
        ).subscribe();

        Thread.sleep(1000L);

        verify(paddyPowerService, atLeastOnce()).receive(any());
        verify(betwayService, atLeastOnce()).receive(any());


    }

}