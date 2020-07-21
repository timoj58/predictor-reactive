package com.timmytime.predictorplayersreactive.repo;

import com.timmytime.predictorplayersreactive.model.PlayersTrainingHistory;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlayersTrainingHistoryRepo extends ReactiveMongoRepository<PlayersTrainingHistory, UUID> {
   Mono<PlayersTrainingHistory> findFirstOrderByDateDesc();
   Mono<PlayersTrainingHistory> findByCompletedFalse();
}
