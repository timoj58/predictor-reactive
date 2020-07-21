package com.timmytime.predictordatareactive.service;

import com.timmytime.predictordatareactive.model.Lineup;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.sound.sampled.Line;
import java.util.UUID;

public interface LineupService {
    Mono<Lineup> find(UUID id);
    void delete(UUID id);
    Mono<Lineup> save(Lineup lineup);
    Mono<Void> deleteByMatch(UUID matchId);
    Flux<Lineup> findByMatch(UUID matchId);
}
