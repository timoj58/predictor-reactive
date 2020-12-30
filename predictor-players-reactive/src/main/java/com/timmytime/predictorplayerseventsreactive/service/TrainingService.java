package com.timmytime.predictorplayerseventsreactive.service;

import com.timmytime.predictorplayerseventsreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayerseventsreactive.model.PlayersTrainingHistory;

public interface TrainingService {
    void train(FantasyEventTypes type);
    void train(PlayersTrainingHistory playersTrainingHistory);
}
