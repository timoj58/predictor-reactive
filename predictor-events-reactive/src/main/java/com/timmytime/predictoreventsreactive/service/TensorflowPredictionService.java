package com.timmytime.predictoreventsreactive.service;

import com.timmytime.predictoreventsreactive.request.TensorflowPrediction;
import reactor.core.publisher.Mono;

public interface TensorflowPredictionService {
    void predict(TensorflowPrediction tensorflowPrediction);
}
