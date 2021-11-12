package com.timmytime.predictorplayerseventsreactive.repo;

import com.timmytime.predictorplayerseventsreactive.model.PlayerMatch;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlayerMatchRepo extends MongoRepository<PlayerMatch, UUID> {
    List<PlayerMatch> findByDateBetween(LocalDate startDate, LocalDate endDate);
    Optional<PlayerMatch> findByDateAndPlayerId(LocalDate date, UUID playerId);
}
