package com.timmytime.predictorplayersreactive.handler;

import com.timmytime.predictorplayersreactive.service.InitService;
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
        return ServerResponse.ok().build(initService.init(
                request.queryParam("from").orElse("01-08-2009"),
                request.queryParam("to").orElse("01-08-2009")
        ));
    }
}
