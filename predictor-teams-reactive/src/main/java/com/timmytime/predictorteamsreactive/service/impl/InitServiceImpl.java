package com.timmytime.predictorteamsreactive.service.impl;

import com.timmytime.predictorteamsreactive.service.InitService;
import com.timmytime.predictorteamsreactive.service.TrainingHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
public class InitServiceImpl implements InitService {
    private final TrainingHistoryService trainingHistoryService;
    @Override
    public Mono<Void> init() {
        CompletableFuture.runAsync(trainingHistoryService::init);
        return Mono.empty();
    }
}
