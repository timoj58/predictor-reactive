package com.timmytime.predictoreventsreactive.service;

import com.timmytime.predictoreventsreactive.enumerator.Predictions;
import com.timmytime.predictoreventsreactive.request.Prediction;
import com.timmytime.predictoreventsreactive.request.TensorflowPrediction;

public interface TensorflowPredictionService {
    void predict(TensorflowPrediction tensorflowPrediction);
}
