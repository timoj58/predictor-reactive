package com.timmytime.predictoreventsreactive.service;

import org.json.JSONObject;

import java.util.UUID;
import java.util.function.Consumer;

public interface PredictionResultService {
    void addCountry(String country);
    void result(UUID id, JSONObject result, Consumer<UUID> fix);
}
