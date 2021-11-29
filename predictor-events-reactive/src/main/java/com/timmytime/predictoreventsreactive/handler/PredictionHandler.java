package com.timmytime.predictoreventsreactive.handler;

import com.timmytime.predictoreventsreactive.request.TensorflowPrediction;
import com.timmytime.predictoreventsreactive.service.TensorflowPredictionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Component
public class PredictionHandler {

    private final TensorflowPredictionService tensorflowPredictionService;

    public Mono<ServerResponse> predict(ServerRequest request) {

        return ServerResponse.ok().build(
                request.bodyToMono(TensorflowPrediction.class)
                        .doOnNext(tensorflowPredictionService::predict)
                        .thenEmpty(Mono.empty()));

    }

}
