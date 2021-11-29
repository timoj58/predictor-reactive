package com.timmytime.predictorplayersreactive.repo;

import com.timmytime.predictorplayersreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayersreactive.model.PlayersTrainingHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlayersTrainingHistoryRepo extends MongoRepository<PlayersTrainingHistory, UUID> {
    PlayersTrainingHistory findFirstByTypeOrderByDateDesc(FantasyEventTypes type);
}
