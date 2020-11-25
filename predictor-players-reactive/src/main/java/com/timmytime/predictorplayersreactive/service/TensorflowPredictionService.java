package com.timmytime.predictorplayersreactive.service;


import com.timmytime.predictorplayersreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayersreactive.request.PlayerEventOutcomeCsv;
import com.timmytime.predictorplayersreactive.request.TensorflowPrediction;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Consumer;

public interface TensorflowPredictionService {
    void predict(TensorflowPrediction tensorflowPrediction);
    void init(String type);
    void destroy(String type);
}
