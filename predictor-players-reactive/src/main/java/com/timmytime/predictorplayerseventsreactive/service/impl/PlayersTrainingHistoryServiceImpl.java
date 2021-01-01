package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayerseventsreactive.model.PlayersTrainingHistory;
import com.timmytime.predictorplayerseventsreactive.repo.PlayersTrainingHistoryRepo;
import com.timmytime.predictorplayerseventsreactive.service.PlayersTrainingHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service("playersTrainingHistoryService")
public class PlayersTrainingHistoryServiceImpl implements PlayersTrainingHistoryService {

    private final PlayersTrainingHistoryRepo playersTrainingHistoryRepo;

    @Override
    public Mono<PlayersTrainingHistory> find(UUID id) {
        return Mono.just(playersTrainingHistoryRepo.findById(id).get());
    }

    @Override
    public Mono<PlayersTrainingHistory> save(PlayersTrainingHistory trainingHistory) {
        return Mono.just(playersTrainingHistoryRepo.save(trainingHistory));
    }

    @Override
    public void saveNormal(PlayersTrainingHistory trainingHistory) {
        playersTrainingHistoryRepo.save(trainingHistory);
    }

    @Override
    public Mono<PlayersTrainingHistory> find(FantasyEventTypes type) {
        return Mono.just(playersTrainingHistoryRepo.findFirstByTypeOrderByDateDesc(type));
    }

    @Override
    public Optional<PlayersTrainingHistory> findOptional(FantasyEventTypes type) {
        return Optional.ofNullable(playersTrainingHistoryRepo.findFirstByTypeOrderByDateDesc(type));
    }
}
