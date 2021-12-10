package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.facade.WebClientFacade;
import com.timmytime.predictorplayerseventsreactive.model.Event;
import com.timmytime.predictorplayerseventsreactive.service.EventsService;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EventsServiceImplTest {
    private final WebClientFacade webClientFacade = mock(WebClientFacade.class);
    private final EventsService eventsService
            = new EventsServiceImpl("data", webClientFacade);

    @Test
    void get() {
        when(webClientFacade.getEvents(anyString()))
                .thenReturn(Flux.just(Event.builder().competition("greece_1").build()));

        assertTrue(eventsService.get("greece_1").blockFirst().getCompetition().equals("greece_1"));
    }

    @Test
    void sanity(){
        String s = "s";
        var res = ((Object)s).equals("s");
        System.out.println(res);
    }

}