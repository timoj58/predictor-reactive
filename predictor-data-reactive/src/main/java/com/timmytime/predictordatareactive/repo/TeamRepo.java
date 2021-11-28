package com.timmytime.predictordatareactive.repo;

import com.timmytime.predictordatareactive.model.Team;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface TeamRepo extends ReactiveMongoRepository<Team, UUID> {
    Flux<Team> findByCountry(String country);
    Mono<Team> findByCompetitionAndLabel(String competition, String label);
}
