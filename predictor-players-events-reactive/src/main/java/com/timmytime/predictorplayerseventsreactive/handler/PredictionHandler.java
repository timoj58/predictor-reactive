package com.timmytime.predictorplayerseventsreactive.handler;

import com.timmytime.predictorplayerseventsreactive.request.TensorflowPrediction;
import com.timmytime.predictorplayerseventsreactive.service.TensorflowPredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class PredictionHandler {

    private final TensorflowPredictionService tensorflowPredictionService;

    public Mono<ServerResponse> predict(ServerRequest request) {

        return ServerResponse.ok()
                .build(request.bodyToMono(TensorflowPrediction.class)
                        .doOnNext(tensorflowPredictionService::predict)
                        .thenEmpty(Mono.empty()));
    }
}
