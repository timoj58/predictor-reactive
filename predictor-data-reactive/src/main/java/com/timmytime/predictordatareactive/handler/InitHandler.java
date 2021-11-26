package com.timmytime.predictordatareactive.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.timmytime.predictordatareactive.service.InitService;
import com.timmytime.predictordatareactive.service.PlayerService;
import com.timmytime.predictordatareactive.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class InitHandler {

    private final InitService initService;

    public Mono<ServerResponse> init(ServerRequest request) {
        return ServerResponse.ok().build(initService.init());
    }
}
