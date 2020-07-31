package com.timmytime.predictorteamsreactive.service;

import com.timmytime.predictorteamsreactive.enumerator.Training;
import com.timmytime.predictorteamsreactive.model.Message;
import com.timmytime.predictorteamsreactive.model.TrainingHistory;

import java.util.UUID;

public interface TrainingHistoryService {
    TrainingHistory create(Training type, Message message);
    TrainingHistory find(UUID id);
    TrainingHistory save(TrainingHistory trainingHistory);
    Boolean finished(Training type);
    TrainingHistory find(Training type, String country);
    TrainingHistory clone(TrainingHistory trainingHistory);
}
