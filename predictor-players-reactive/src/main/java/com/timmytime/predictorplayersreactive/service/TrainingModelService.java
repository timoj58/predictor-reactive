package com.timmytime.predictorplayersreactive.service;

import com.timmytime.predictorplayersreactive.model.PlayersTrainingHistory;

public interface TrainingModelService {
    void create();

    void next(PlayersTrainingHistory playersTrainingHistory);
}
