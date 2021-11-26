package com.timmytime.predictordatareactive.service.impl;

import com.timmytime.predictordatareactive.service.InitService;
import com.timmytime.predictordatareactive.service.PlayerService;
import com.timmytime.predictordatareactive.service.ResultService;
import com.timmytime.predictordatareactive.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
public class InitServiceImpl implements InitService {

    private final TeamService teamService;
    private final PlayerService playerService;
    private final ResultService resultService;

    @Override
    public Mono<Void> init() {
        CompletableFuture.runAsync(teamService::init)
                .thenRun(playerService::init)
                .thenRun(resultService::init);

        return Mono.empty();
    }
}
