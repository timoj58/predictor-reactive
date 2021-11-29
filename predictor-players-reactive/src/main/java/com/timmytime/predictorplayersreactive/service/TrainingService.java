package com.timmytime.predictorplayersreactive.service;

import com.timmytime.predictorplayersreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayersreactive.model.PlayersTrainingHistory;

public interface TrainingService {
    void train(FantasyEventTypes type);

    void train(PlayersTrainingHistory playersTrainingHistory);

    FantasyEventTypes firstTrainingEvent();
}
