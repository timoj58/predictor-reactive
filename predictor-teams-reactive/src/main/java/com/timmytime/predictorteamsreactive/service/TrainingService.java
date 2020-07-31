package com.timmytime.predictorteamsreactive.service;
import com.timmytime.predictorteamsreactive.enumerator.Training;
import com.timmytime.predictorteamsreactive.model.TrainingHistory;

public interface TrainingService {
    void train();
    Boolean train(TrainingHistory trainingHistory);
    TrainingHistory init(Training type, String country);
}
