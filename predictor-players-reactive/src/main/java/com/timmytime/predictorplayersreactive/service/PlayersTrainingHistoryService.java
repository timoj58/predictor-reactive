package com.timmytime.predictorplayersreactive.service;

import com.timmytime.predictorplayersreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayersreactive.model.PlayersTrainingHistory;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PlayersTrainingHistoryService {
    Mono<PlayersTrainingHistory> find(UUID id);

    Mono<PlayersTrainingHistory> save(PlayersTrainingHistory trainingHistory);

    Mono<PlayersTrainingHistory> find(FantasyEventTypes type);
}
