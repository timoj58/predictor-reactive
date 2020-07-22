package com.timmytime.predictorteamsreactive.service;

import com.timmytime.predictorteamsreactive.model.TrainingHistory;

public interface TrainingService {
    void train();
    void train(TrainingHistory trainingHistory);
}
