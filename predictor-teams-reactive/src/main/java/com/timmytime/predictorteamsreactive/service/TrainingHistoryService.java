package com.timmytime.predictorteamsreactive.service;

import com.timmytime.predictorteamsreactive.enumerator.Training;
import com.timmytime.predictorteamsreactive.model.TrainingHistory;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

public interface TrainingHistoryService {
    TrainingHistory find(UUID id);

    TrainingHistory save(TrainingHistory trainingHistory);

    TrainingHistory find(Training type, String country);

    TrainingHistory next(Training type, String country, Integer interval);

    void completeTraining(TrainingHistory trainingHistory);

    void init(@RequestParam(defaultValue = "01-08-2009") String from,
              @RequestParam(defaultValue = "01-08-2009") String to);
}
