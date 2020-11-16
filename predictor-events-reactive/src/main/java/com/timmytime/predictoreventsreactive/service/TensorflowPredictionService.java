package com.timmytime.predictoreventsreactive.service;

import com.timmytime.predictoreventsreactive.enumerator.Predictions;
import com.timmytime.predictoreventsreactive.request.Prediction;
import com.timmytime.predictoreventsreactive.request.TensorflowPrediction;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Consumer;

public interface TensorflowPredictionService {
    void predict(TensorflowPrediction tensorflowPrediction);
    void setReplayConsumer(Consumer<UUID> replay);
}
