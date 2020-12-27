package com.timmytime.predictorplayerseventsreactive.service;

import com.timmytime.predictorplayerseventsreactive.model.Event;
import reactor.core.publisher.Flux;

public interface EventsService {

    Flux<Event> get(String competition);
}
