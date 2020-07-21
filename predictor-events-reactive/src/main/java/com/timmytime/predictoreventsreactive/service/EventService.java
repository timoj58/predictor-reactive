package com.timmytime.predictoreventsreactive.service;

import com.timmytime.predictoreventsreactive.model.Event;
import reactor.core.publisher.Flux;

public interface EventService {
    Flux<Event> getEvents(String competition);
}
