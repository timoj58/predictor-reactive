package com.timmytime.predictoreventsreactive.service;

import org.json.JSONObject;

import java.util.UUID;
import java.util.function.Consumer;

public interface PredictionResultService {
    void result(UUID id, JSONObject result);
}
