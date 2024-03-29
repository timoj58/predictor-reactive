package com.timmytime.predictoreventdatareactive.service.impl;

import com.timmytime.predictoreventdatareactive.service.EspnService;
import com.timmytime.predictoreventdatareactive.service.InitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
public class InitServiceImpl implements InitService {
    private final EspnService espnService;

    @Override
    public Mono<Void> init() {
        CompletableFuture.runAsync(espnService::init);
        return Mono.empty();
    }
}
