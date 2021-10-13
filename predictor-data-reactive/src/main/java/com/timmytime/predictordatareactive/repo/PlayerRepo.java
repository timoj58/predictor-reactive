package com.timmytime.predictordatareactive.repo;

import com.timmytime.predictordatareactive.model.Player;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlayerRepo extends ReactiveMongoRepository<Player, UUID> {
    Mono<Player> findByLabel(String label);

    Mono<Player> findByEspnId(String espnId);

    Flux<Player> findByLatestTeamIn(List<UUID> teams);

}
