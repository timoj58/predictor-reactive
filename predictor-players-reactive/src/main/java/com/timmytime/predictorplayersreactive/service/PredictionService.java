package com.timmytime.predictorplayersreactive.service;

import org.json.JSONObject;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PredictionService {
    void start(String country);
    void result(UUID id, JSONObject result);
    Mono<Void> fix();
    Mono<Long> toFix();
}
