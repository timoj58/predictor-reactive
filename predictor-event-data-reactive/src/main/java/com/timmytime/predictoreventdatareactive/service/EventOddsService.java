package com.timmytime.predictoreventdatareactive.service;

import com.timmytime.predictoreventdatareactive.enumerator.Providers;
import com.timmytime.predictoreventdatareactive.model.EventOdds;
import com.timmytime.predictoreventdatareactive.response.Event;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface EventOddsService {
    Mono<EventOdds> create(EventOdds eventOdds);

    Mono<EventOdds> findEvent(String provider, String event, LocalDateTime eventDate, Double price, List<UUID> teams);

    Mono<Void> delete(Providers providers);

    Flux<Event> getEvents(@PathVariable String competition);
}
