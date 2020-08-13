package com.timmytime.predictordatareactive.repo;

import com.timmytime.predictordatareactive.model.LineupPlayer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface LineupPlayerRepo extends ReactiveMongoRepository<LineupPlayer, UUID> {
    Mono<Void> deleteByMatchId(UUID matchId);
    Flux<LineupPlayer> findByPlayerAndDateBetween(UUID player, LocalDateTime from, LocalDateTime to);
    Flux<LineupPlayer> findByPlayer(UUID player);
}
