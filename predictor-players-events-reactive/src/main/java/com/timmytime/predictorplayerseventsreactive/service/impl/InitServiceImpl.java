package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.service.FantasyOutcomeService;
import com.timmytime.predictorplayerseventsreactive.service.InitService;
import com.timmytime.predictorplayerseventsreactive.service.TensorflowPredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class InitServiceImpl implements InitService {
    private final FantasyOutcomeService fantasyOutcomeService;
    private final TensorflowPredictionService tensorflowPredictionService;

    @Override
    public Mono<Void> init() {
        CompletableFuture.runAsync(fantasyOutcomeService::init)
                .thenRun(() -> {
                    //init machine
                    Flux.fromStream(
                            Stream.of("assists", "goals", "yellow")
                    ).subscribe(tensorflowPredictionService::init);

                });
        return Mono.empty();
    }
}
