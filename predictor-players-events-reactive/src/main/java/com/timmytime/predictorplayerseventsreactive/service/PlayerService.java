package com.timmytime.predictorplayerseventsreactive.service;

import com.timmytime.predictorplayerseventsreactive.model.Player;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface PlayerService {

    Flux<Player> get(String competition);

    Flux<Player> get(String competition, UUID team);

    Mono<Player> get(UUID id);

    Flux<Player> get();

    Flux<Player> byMatch(
            @PathVariable String competition,
            @RequestParam UUID home,
            @RequestParam UUID away);
}
