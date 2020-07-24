package com.timmytime.predictorplayersreactive.repo;

import com.timmytime.predictorplayersreactive.model.FantasyOutcome;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import java.util.UUID;

public interface FantasyOutcomeRepo extends ReactiveMongoRepository<FantasyOutcome, UUID> {

}
