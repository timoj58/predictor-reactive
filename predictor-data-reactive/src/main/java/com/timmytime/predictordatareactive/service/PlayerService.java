package com.timmytime.predictordatareactive.service;

import com.timmytime.predictordatareactive.model.Player;
import org.json.JSONArray;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface PlayerService {
    Mono<Player> find(UUID id);

    Mono<Player> save(Player player);

    List<Mono<Player>> process(JSONArray players);

    Flux<Player> findByCompetition(@PathVariable String competition, @RequestParam String date);

    Flux<Player> findAll();

 }
