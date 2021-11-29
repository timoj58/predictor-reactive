package com.timmytime.predictorplayersreactive.service.impl;

import com.timmytime.predictorplayersreactive.service.InitService;
import com.timmytime.predictorplayersreactive.service.PlayersTrainingHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
public class InitServiceImpl implements InitService {
    private final PlayersTrainingHistoryService trainingHistoryService;
    @Override
    public Mono<Void> init(String from, String to) {
        CompletableFuture.runAsync(() -> trainingHistoryService.init(from, to));
        return Mono.empty();
    }
}
