package com.timmytime.predictorteamsreactive.service;


import com.timmytime.predictorteamsreactive.model.TrainingHistory;

public interface TensorflowTrainService {
    void train(TrainingHistory trainingHistory);
}
