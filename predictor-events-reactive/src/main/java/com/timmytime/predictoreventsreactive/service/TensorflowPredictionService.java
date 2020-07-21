package com.timmytime.predictoreventsreactive.service;

import com.timmytime.predictoreventsreactive.enumerator.Predictions;
import com.timmytime.predictoreventsreactive.request.Prediction;

public interface TensorflowPredictionService {
    void predict(Predictions predictions, Prediction prediction, String country);
}
