package com.timmytime.predictorplayerseventsreactive.service;

import reactor.core.publisher.Mono;

public interface PredictionService {
    void start(String country);

    void reProcess();

    Mono<Long> outstanding();
}
