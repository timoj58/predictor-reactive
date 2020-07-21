package com.timmytime.predictoreventsreactive.repo;


import com.timmytime.predictoreventsreactive.model.EventOutcome;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventOutcomeRepo extends ReactiveMongoRepository<EventOutcome, UUID> {

}
