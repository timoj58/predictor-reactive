package com.timmytime.predictorplayerseventsreactive.service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.UUID;
import java.util.function.Consumer;

public interface PredictionResultService {
    void result(JSONArray result);
}
