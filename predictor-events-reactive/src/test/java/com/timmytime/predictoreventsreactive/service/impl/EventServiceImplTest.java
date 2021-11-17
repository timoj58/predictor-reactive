package com.timmytime.predictoreventsreactive.service.impl;

import com.timmytime.predictoreventsreactive.facade.WebClientFacade;
import com.timmytime.predictoreventsreactive.model.Event;
import com.timmytime.predictoreventsreactive.service.EventService;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EventServiceImplTest {

    private final WebClientFacade webClientFacade = mock(WebClientFacade.class);

    private final EventService eventService
            = new EventServiceImpl("events", webClientFacade);

    @Test
    void getEvents(){
        when(webClientFacade.getEvents( "events/events/england"))
                .thenReturn(Flux.just(Event.builder().competition("england_1").build()));
        assertTrue(eventService.getEvents("england").blockFirst().getCompetition().equals("england_1"));
    }

}