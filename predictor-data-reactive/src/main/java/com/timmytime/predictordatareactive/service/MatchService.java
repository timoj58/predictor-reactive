package com.timmytime.predictordatareactive.service;

import com.timmytime.predictordatareactive.model.Match;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MatchService {
    Mono<Match> find(@PathVariable UUID id);

    void delete(UUID id);

    Mono<Match> save(Match match);

    Mono<Match> getMatch(@RequestParam UUID home, @RequestParam UUID away, @RequestParam String date);

    Mono<Match> getMatchByOpponent(
            @PathVariable UUID opponent,
            @RequestParam Boolean home,
            @RequestParam String date);

    Flux<Match> getMatches(@PathVariable UUID team);

    Flux<Match> getMatchesByCountry(
            @PathVariable String country,
            @PathVariable String fromDate,
            @PathVariable String toDate);


}
