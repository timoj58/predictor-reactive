package com.timmytime.predictorplayersreactive.service;

import org.json.JSONObject;

import java.util.UUID;

public interface PredictionService {
    void start(String country);
    void result(UUID id, JSONObject result);
}
