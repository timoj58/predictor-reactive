package com.timmytime.predictoreventdatareactive.handler;

import com.timmytime.predictoreventdatareactive.response.Event;
import com.timmytime.predictoreventdatareactive.service.EventOddsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class EventHandler {

    private final EventOddsService eventOddsService;


    public Mono<ServerResponse> events(ServerRequest request) {

        return ServerResponse.ok().body(
                eventOddsService.getEvents(
                        request.pathVariable("competition")
                ),
                Event.class
        );
    }
}
