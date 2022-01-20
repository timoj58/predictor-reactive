package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.service.FantasyOutcomeService;
import com.timmytime.predictorplayerseventsreactive.service.InitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
public class InitServiceImpl implements InitService {
    private final FantasyOutcomeService fantasyOutcomeService;

    @Override
    public Mono<Void> init() {
        CompletableFuture.runAsync(fantasyOutcomeService::init);

        return Mono.empty();
    }
}
