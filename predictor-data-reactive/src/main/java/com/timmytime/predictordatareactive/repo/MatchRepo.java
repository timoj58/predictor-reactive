package com.timmytime.predictordatareactive.repo;


import com.timmytime.predictordatareactive.model.Match;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MatchRepo extends ReactiveMongoRepository<Match, UUID> {

    Flux<Match> findByHomeTeam(UUID homeTeam);
    Flux<Match> findByAwayTeam(UUID awayTeam);
    Flux<Match> findByHomeTeamInAndDateBetween(List<UUID> ids, LocalDateTime fromDate, LocalDateTime toDate);
    Flux<Match> findByAwayTeamInAndDateBetween(List<UUID> ids, LocalDateTime fromDate, LocalDateTime toDate);
    Mono<Match> findByHomeTeamAndAwayTeamAndDate(UUID homeTeam, UUID awayTeam, LocalDateTime date);
    Flux<Match> findByHomeTeamAndAwayTeam(UUID home, UUID away);

 }
