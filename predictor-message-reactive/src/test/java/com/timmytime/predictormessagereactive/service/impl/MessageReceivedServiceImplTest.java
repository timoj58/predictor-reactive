package com.timmytime.predictormessagereactive.service.impl;

import com.timmytime.predictormessagereactive.enumerator.Event;
import com.timmytime.predictormessagereactive.enumerator.EventType;
import com.timmytime.predictormessagereactive.request.Message;
import com.timmytime.predictormessagereactive.service.MessageReceivedService;
import com.timmytime.predictormessagereactive.service.OrchestrationService;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageReceivedServiceImplTest {

    private final OrchestrationService orchestrationService = mock(OrchestrationService.class);
    private final MessageReceivedService messageReceivedService
            = new MessageReceivedServiceImpl(orchestrationService);

    @Test
    void receive(){

        messageReceivedService.receive(Mono.just(Message.builder()
                .event(Event.DATA_LOADED)
                .eventType(EventType.ALL).build())).subscribe();

        verify(orchestrationService, atLeastOnce()).process(any());

    }


}