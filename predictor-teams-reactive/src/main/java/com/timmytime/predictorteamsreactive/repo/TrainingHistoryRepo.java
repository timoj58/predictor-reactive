package com.timmytime.predictorteamsreactive.repo;

import com.timmytime.predictorteamsreactive.enumerator.Training;
import com.timmytime.predictorteamsreactive.model.TrainingHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TrainingHistoryRepo extends MongoRepository<TrainingHistory, UUID> {
    TrainingHistory findByCountryAndCompletedFalse(String country);

    List<TrainingHistory> findByTypeAndCountryOrderByDateDesc(Training type, String country);

    List<TrainingHistory> findByTypeAndCompletedFalse(Training type);
}
