package com.timmytime.predictoreventsreactive.service;

import org.json.JSONObject;

import java.util.UUID;

public interface PredictionResultService {
    void result(UUID id, JSONObject result);
}
