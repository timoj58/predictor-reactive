package com.timmytime.predictorplayerseventsreactive.handler;

import com.timmytime.predictorplayerseventsreactive.service.PredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class PredictionHandler {

    private final PredictionService predictionService;

    public Mono<ServerResponse> fix(ServerRequest request) {
        return ServerResponse.ok().build(predictionService.fix());
    }

}
