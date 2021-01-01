package com.timmytime.predictorplayerseventsreactive.repo;

import com.timmytime.predictorplayerseventsreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayerseventsreactive.model.PlayersTrainingHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlayersTrainingHistoryRepo extends MongoRepository<PlayersTrainingHistory, UUID> {
    PlayersTrainingHistory findFirstByTypeOrderByDateDesc(FantasyEventTypes type);
}
