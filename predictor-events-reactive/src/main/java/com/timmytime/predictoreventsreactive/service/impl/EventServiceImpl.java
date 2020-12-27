package com.timmytime.predictoreventsreactive.service.impl;

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

    @Autowired
    public EventServiceImpl(
            @Value("${clients.event-data}") String eventDataHost
    ) {
        this.eventDataHost = eventDataHost;
    }

    @Override
    public Flux<Event> getEvents(String competition) {
        return WebClient.builder().build()
                .get()
                .uri(eventDataHost + "/events/" + competition)
                .retrieve()
                .bodyToFlux(Event.class);
    }
}
