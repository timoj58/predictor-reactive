package com.timmytime.predictordatareactive.service;

import com.timmytime.predictordatareactive.model.TeamStats;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TeamStatsService {
    Mono<TeamStats> find(UUID id);
    void delete(UUID id);
    Mono<TeamStats> save(TeamStats teamStats);

}
