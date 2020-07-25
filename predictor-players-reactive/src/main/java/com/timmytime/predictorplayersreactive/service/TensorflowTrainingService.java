package com.timmytime.predictorplayersreactive.service;

import java.time.LocalDateTime;
import java.util.UUID;

public interface TensorflowTrainingService {
    void train(UUID id);
}
