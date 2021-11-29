package com.timmytime.predictoreventsreactive.service;

import com.timmytime.predictoreventsreactive.request.TensorflowPrediction;

public interface TensorflowPredictionService {
    void predict(TensorflowPrediction tensorflowPrediction);
}
