package com.timmytime.predictorplayerseventsreactive.repo;

import com.timmytime.predictorplayerseventsreactive.model.FantasyOutcome;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface FantasyOutcomeRepo extends ReactiveMongoRepository<FantasyOutcome, UUID> {
    Flux<FantasyOutcome> findByPlayerId(UUID player);

    Flux<FantasyOutcome> findByCurrent(Boolean current);

    Flux<FantasyOutcome> findByPredictionNull();
}
