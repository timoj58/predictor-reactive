package com.timmytime.predictorplayerseventsreactive.service;

import reactor.core.publisher.Mono;

public interface PredictionService {
    void start(String country);

    Mono<Void> fix();

    void reProcess();

    Mono<Long> outstanding();
}
