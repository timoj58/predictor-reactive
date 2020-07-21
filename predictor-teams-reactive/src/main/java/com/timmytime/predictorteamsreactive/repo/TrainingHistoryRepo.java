package com.timmytime.predictorteamsreactive.repo;

import com.timmytime.predictorteamsreactive.model.TrainingHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrainingHistoryRepo extends MongoRepository<TrainingHistory, UUID> {
   TrainingHistory findByCountryAndCompletedFalse(String country);
   List<TrainingHistory> findByCountryOrderByDateDesc(String country);
   Optional<TrainingHistory> findByCompletedFalse();
}
