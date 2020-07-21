package com.timmytime.predictorplayersreactive.service;

import com.timmytime.predictorplayersreactive.model.Event;
import reactor.core.publisher.Flux;

public interface EventsService {

    Flux<Event> get(String competition);
}
