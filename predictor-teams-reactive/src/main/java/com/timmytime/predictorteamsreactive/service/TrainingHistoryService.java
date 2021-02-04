package com.timmytime.predictorteamsreactive.service;

import com.timmytime.predictorteamsreactive.enumerator.Training;
import com.timmytime.predictorteamsreactive.model.TrainingHistory;

import java.util.UUID;

public interface TrainingHistoryService {
    TrainingHistory find(UUID id);

    TrainingHistory save(TrainingHistory trainingHistory);

    TrainingHistory find(Training type, String country);

    TrainingHistory next(Training type, String country, Integer interval);

    void completeTraining(TrainingHistory trainingHistory);
}
