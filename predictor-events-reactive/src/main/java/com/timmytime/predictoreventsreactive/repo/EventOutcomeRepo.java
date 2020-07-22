package com.timmytime.predictoreventsreactive.repo;


import com.timmytime.predictoreventsreactive.model.EventOutcome;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventOutcomeRepo extends ReactiveMongoRepository<EventOutcome, UUID> {
    Flux<EventOutcome> findByCompetitionInAndSuccessNull(List<String> competition);
    Flux<EventOutcome> findByCompetitionInAndPreviousEventTrue(List<String> competition);

}
