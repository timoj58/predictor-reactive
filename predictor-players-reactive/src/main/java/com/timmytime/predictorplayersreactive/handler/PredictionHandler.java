package com.timmytime.predictorplayersreactive.handler;

import com.timmytime.predictorplayersreactive.service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class PredictionHandler {

    private final PredictionService predictionService;

    @Autowired
    public PredictionHandler(
            PredictionService predictionService
    ){
        this.predictionService = predictionService;
    }

    public Mono<ServerResponse> fix(ServerRequest request) {
        return ServerResponse.ok().build(predictionService.fix());
    }

}
