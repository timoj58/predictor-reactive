package com.timmytime.predictorteamsreactive.service;

import com.timmytime.predictorteamsreactive.model.TrainingHistory;

import java.util.function.Function;

public interface TrainingService {
    void train(Function<Integer, TrainingHistory> trainingHistoryFuunction);
}
