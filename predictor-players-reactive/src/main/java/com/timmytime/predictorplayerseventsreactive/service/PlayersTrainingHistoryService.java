package com.timmytime.predictorplayerseventsreactive.service;

import com.timmytime.predictorplayerseventsreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayerseventsreactive.model.PlayersTrainingHistory;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

public interface PlayersTrainingHistoryService {
    Mono<PlayersTrainingHistory> find(UUID id);

    Mono<PlayersTrainingHistory> save(PlayersTrainingHistory trainingHistory);

    void saveNormal(PlayersTrainingHistory trainingHistory);

    Mono<PlayersTrainingHistory> find(FantasyEventTypes type);

    Optional<PlayersTrainingHistory> findOptional(FantasyEventTypes type);

    void init(@RequestParam(defaultValue = "01-08-2009") String from,
              @RequestParam(defaultValue = "01-08-2009") String to);
}
