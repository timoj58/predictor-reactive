package com.timmytime.predictorteamsreactive.service;

import com.timmytime.predictorteamsreactive.model.Message;
import com.timmytime.predictorteamsreactive.model.TrainingHistory;

import java.util.UUID;

public interface TrainingHistoryService {
    TrainingHistory create(Message message);
    TrainingHistory find(UUID id);
    void save(TrainingHistory trainingHistory);
    Boolean finished();
}
