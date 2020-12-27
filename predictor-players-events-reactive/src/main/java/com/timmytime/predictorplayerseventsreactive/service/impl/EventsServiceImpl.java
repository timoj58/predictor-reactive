package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.facade.WebClientFacade;
import com.timmytime.predictorplayerseventsreactive.model.Event;
import com.timmytime.predictorplayerseventsreactive.service.EventsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service("eventsService")
public class EventsServiceImpl implements EventsService {

    private final String eventDataHost;
    private final WebClientFacade webClientFacade;

    @Autowired
    public EventsServiceImpl(
            @Value("${clients.event-data}") String eventDataHost,
            WebClientFacade webClientFacade
    ) {
        this.eventDataHost = eventDataHost;
        this.webClientFacade = webClientFacade;
    }

    @Override
    public Flux<Event> get(String competition) {
        log.info("getting events for {}", competition);
        return webClientFacade.getEvents(eventDataHost + "/events/" + competition);
    }
}
