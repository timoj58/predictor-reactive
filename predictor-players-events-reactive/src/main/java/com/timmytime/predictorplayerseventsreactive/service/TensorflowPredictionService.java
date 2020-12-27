package com.timmytime.predictorplayerseventsreactive.service;


import com.timmytime.predictorplayerseventsreactive.request.TensorflowPrediction;

public interface TensorflowPredictionService {
    void predict(TensorflowPrediction tensorflowPrediction);

    void init(String type);

    void destroy(String type);
}
