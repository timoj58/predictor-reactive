package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.service.InitService;
import com.timmytime.predictorplayerseventsreactive.service.PlayersTrainingHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
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
