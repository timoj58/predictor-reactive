package com.timmytime.predictordatareactive.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictordatareactive.model.Result;
import com.timmytime.predictordatareactive.service.ResultService;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MessageReceivedServiceImplTest {

    private static final ResultService resultService = mock(ResultService.class);
    private final MessageReceivedServiceImpl messageService
            = new MessageReceivedServiceImpl(resultService);


    @BeforeAll
    public static void setUp() {
        when(resultService.findByMatch(any())).thenReturn(Mono.just(new Result()));

    }

    @Test
    public void messageCreateTest() throws JSONException, JsonProcessingException, InterruptedException {

        JSONObject message = new JSONObject().put("matchId", 2)
                .put("type", "result");

        messageService.receive(Mono.just(new ObjectMapper().readTree(message.toString()))).subscribe();

        Thread.sleep(1000L);

        verify(resultService, atLeastOnce()).process(any());

    }

}