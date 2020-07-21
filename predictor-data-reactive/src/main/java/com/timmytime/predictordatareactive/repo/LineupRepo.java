package com.timmytime.predictordatareactive.repo;


import com.timmytime.predictordatareactive.model.Lineup;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface LineupRepo extends ReactiveMongoRepository<Lineup, UUID> {
    Mono<Void> deleteByMatchId(UUID matchId);
    Flux<Lineup> findByMatchId(UUID matchId);
}
