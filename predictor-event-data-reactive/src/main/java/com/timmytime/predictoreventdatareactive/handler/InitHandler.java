package com.timmytime.predictoreventdatareactive.handler;

import com.timmytime.predictoreventdatareactive.service.InitService;
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
