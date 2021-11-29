package com.timmytime.predictorplayersreactive.service;

import java.util.UUID;

public interface TensorflowTrainingService {
    void train(UUID id);
}
