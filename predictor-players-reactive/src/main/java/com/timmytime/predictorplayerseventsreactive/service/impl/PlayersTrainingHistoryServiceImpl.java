package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayerseventsreactive.model.PlayersTrainingHistory;
import com.timmytime.predictorplayerseventsreactive.repo.PlayersTrainingHistoryRepo;
import com.timmytime.predictorplayerseventsreactive.service.PlayersTrainingHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Service("playersTrainingHistoryService")
public class PlayersTrainingHistoryServiceImpl implements PlayersTrainingHistoryService {

    private final PlayersTrainingHistoryRepo playersTrainingHistoryRepo;

    @Override
    public Mono<PlayersTrainingHistory> find(UUID id) {
        return playersTrainingHistoryRepo.findById(id);
    }

    @Override
    public Mono<PlayersTrainingHistory> save(PlayersTrainingHistory trainingHistory) {
        return playersTrainingHistoryRepo.save(trainingHistory);
    }

    @Override
    public Mono<PlayersTrainingHistory> find(FantasyEventTypes type) {
        return playersTrainingHistoryRepo.findFirstByTypeOrderByDateDesc(type);
    }
}
