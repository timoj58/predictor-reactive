package com.timmytime.predictormessagereactive.repo;

import com.timmytime.predictormessagereactive.model.PredictorCycle;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PredictorCycleRepo extends ReactiveMongoRepository<PredictorCycle, UUID> {
}
