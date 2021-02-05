package com.timmytime.predictorplayerseventsreactive.service;

import com.timmytime.predictorplayerseventsreactive.model.PlayersTrainingHistory;

public interface TrainingModelService {
    void create();

    void next(PlayersTrainingHistory playersTrainingHistory);
}
