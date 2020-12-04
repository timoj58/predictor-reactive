package com.timmytime.predictorplayersreactive.service.impl;

import com.timmytime.predictorplayersreactive.facade.WebClientFacade;
import com.timmytime.predictorplayersreactive.model.Event;
import com.timmytime.predictorplayersreactive.service.EventsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service("eventsService")
public class EventsServiceImpl implements EventsService {

    private final Logger log = LoggerFactory.getLogger(EventsServiceImpl.class);

    private final String eventDataHost;
    private final WebClientFacade webClientFacade;

    @Autowired
    public EventsServiceImpl(
            @Value("${event.data.host}") String eventDataHost,
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
