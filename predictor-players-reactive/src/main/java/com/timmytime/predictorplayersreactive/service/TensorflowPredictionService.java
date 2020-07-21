package com.timmytime.predictorplayersreactive.service;


import com.timmytime.predictorplayersreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayersreactive.request.PlayerEventOutcomeCsv;

public interface TensorflowPredictionService {
    void predict(FantasyEventTypes fantasyEventTypes, PlayerEventOutcomeCsv playerEventOutcomeCsv);
    void init(String type);
    void destroy(String type);
}
