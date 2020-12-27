package com.timmytime.predictorplayerseventsreactive.repo;

import com.timmytime.predictorplayerseventsreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayerseventsreactive.model.PlayersTrainingHistory;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface PlayersTrainingHistoryRepo extends ReactiveMongoRepository<PlayersTrainingHistory, UUID> {
    Mono<PlayersTrainingHistory> findFirstByTypeOrderByDateDesc(FantasyEventTypes type);
}
