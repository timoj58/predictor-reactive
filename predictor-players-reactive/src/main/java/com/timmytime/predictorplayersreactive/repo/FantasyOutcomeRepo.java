package com.timmytime.predictorplayersreactive.repo;

import com.timmytime.predictorplayersreactive.model.FantasyOutcome;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FantasyOutcomeRepo extends ReactiveMongoRepository<FantasyOutcome, UUID> {

}
