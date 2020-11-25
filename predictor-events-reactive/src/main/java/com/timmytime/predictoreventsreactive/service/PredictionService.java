package com.timmytime.predictoreventsreactive.service;

import reactor.core.publisher.Mono;

public interface PredictionService {
    void start(String country);
    Mono<Void> fix();
    void retryMissing();
}
