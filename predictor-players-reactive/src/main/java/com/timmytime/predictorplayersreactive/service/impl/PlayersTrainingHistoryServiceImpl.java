package com.timmytime.predictorplayersreactive.service.impl;

import com.timmytime.predictorplayersreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayersreactive.model.PlayersTrainingHistory;
import com.timmytime.predictorplayersreactive.repo.PlayersTrainingHistoryRepo;
import com.timmytime.predictorplayersreactive.service.PlayersTrainingHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service("playersTrainingHistoryService")
public class PlayersTrainingHistoryServiceImpl implements PlayersTrainingHistoryService {

    private final PlayersTrainingHistoryRepo playersTrainingHistoryRepo;

    @Autowired
    public PlayersTrainingHistoryServiceImpl(
            PlayersTrainingHistoryRepo playersTrainingHistoryRepo
    ){
        this.playersTrainingHistoryRepo = playersTrainingHistoryRepo;
    }

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
