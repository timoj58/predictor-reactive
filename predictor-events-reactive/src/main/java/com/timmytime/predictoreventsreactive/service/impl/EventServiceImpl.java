package com.timmytime.predictoreventsreactive.service.impl;

import com.timmytime.predictoreventsreactive.facade.WebClientFacade;
import com.timmytime.predictoreventsreactive.model.Event;
import com.timmytime.predictoreventsreactive.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service("eventService")
public class EventServiceImpl implements EventService {

    private final String eventDataHost;
    private final WebClientFacade webClientFacade;

    @Autowired
    public EventServiceImpl(
            @Value("${clients.event-data}") String eventDataHost,
            WebClientFacade webClientFacade
    ) {
        this.eventDataHost = eventDataHost;
        this.webClientFacade = webClientFacade;
    }

    @Override
    public Flux<Event> getEvents(String competition) {
        return webClientFacade.getEvents(eventDataHost + "/events/" + competition);
    }
}
