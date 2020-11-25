package com.timmytime.predictorplayersreactive.service;

import org.json.JSONObject;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PredictionService {
    void start(String country);
    Mono<Void> fix();
    void retryMissing();
}
