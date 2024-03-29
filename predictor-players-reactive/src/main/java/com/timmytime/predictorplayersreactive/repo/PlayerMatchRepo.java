package com.timmytime.predictorplayersreactive.repo;

import com.timmytime.predictorplayersreactive.model.PlayerMatch;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlayerMatchRepo extends MongoRepository<PlayerMatch, UUID> {
    List<PlayerMatch> findByDateBetween(LocalDate startDate, LocalDate endDate);

    Optional<PlayerMatch> findByDateAndPlayerId(LocalDate date, UUID playerId);
}
