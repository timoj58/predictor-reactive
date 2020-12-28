package com.timmytime.predictoreventdatareactive.service;

import com.timmytime.predictoreventdatareactive.enumerator.Providers;
import com.timmytime.predictoreventdatareactive.model.EventOdds;
import com.timmytime.predictoreventdatareactive.response.Event;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventOddsService {

    void addToQueue(EventOdds eventOdds);

    Mono<Void> delete(Providers providers);

    Flux<Event> getEvents(@PathVariable String competition);
}
