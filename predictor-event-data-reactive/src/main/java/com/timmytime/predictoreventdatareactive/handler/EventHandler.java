package com.timmytime.predictoreventdatareactive.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.timmytime.predictoreventdatareactive.response.Event;
import com.timmytime.predictoreventdatareactive.service.EventOddsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class EventHandler {

    private final EventOddsService eventOddsService;

    @Autowired
    public EventHandler(
            EventOddsService eventOddsService
    ){
        this.eventOddsService = eventOddsService;
    }

    public Mono<ServerResponse> events(ServerRequest request) {

        return ServerResponse.ok().body(
                eventOddsService.getEvents(
                        request.pathVariable("competition")
                ),
                Event.class
        );
    }
}
