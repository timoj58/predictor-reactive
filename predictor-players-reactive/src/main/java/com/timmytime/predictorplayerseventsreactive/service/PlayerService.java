package com.timmytime.predictorplayerseventsreactive.service;

import com.timmytime.predictorplayerseventsreactive.model.Player;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

public interface PlayerService {
    void load();

    List<Player> get(String competition);

    List<Player> get(String competition, UUID team);

    Player get(UUID id);

    List<Player> get();

    Flux<Player> byMatch(
            @PathVariable String competition,
            @RequestParam UUID home,
            @RequestParam UUID away);

}
