package com.timmytime.predictordatareactive.repo;

import com.timmytime.predictordatareactive.model.Team;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeamRepo extends ReactiveMongoRepository<Team, UUID> {

    Mono<Team> findByLabelIgnoreCase(String label);

    Mono<Team> findByLabelIgnoreCaseAndCountry(String label, String country);

    Mono<Team> findByLabelLikeIgnoreCaseAndCountry(String label, String country);

    Mono<Team> findByLabelRegexIgnoreCaseAndCountry(String regex, String country);

    Flux<Team> findByCountry(String country);

    Flux<Team> findByCountryOrderByLabelDesc(String country);

    Flux<Team> findByCompetition(String competition);

}
