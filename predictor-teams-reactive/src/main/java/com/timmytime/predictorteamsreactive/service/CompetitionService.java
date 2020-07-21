package com.timmytime.predictorteamsreactive.service;

import com.timmytime.predictorteamsreactive.model.Message;
import com.timmytime.predictorteamsreactive.model.TrainingHistory;

import java.util.UUID;

public interface CompetitionService {
    void load(TrainingHistory trainingHistory);
}
