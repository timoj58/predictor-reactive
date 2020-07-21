package com.timmytime.predictorplayersreactive.repo;

import com.timmytime.predictorplayersreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayersreactive.model.FantasyOutcome;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import java.util.List;
import java.util.UUID;

public interface FantasyOutcomeRepo extends ReactiveMongoRepository<FantasyOutcome, UUID> {

    List<FantasyOutcome> findByPlayerIdAndSuccessNull(UUID id);
    List<FantasyOutcome> findByPlayerIdAndSuccessNotNull(UUID id);
    List<FantasyOutcome> findBySuccessNull();
    List<FantasyOutcome> findByPlayerIdInAndSuccessAndFantasyEventType(List<UUID> ids, Boolean success, FantasyEventTypes fantasyEventTypes);

}
