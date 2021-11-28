package com.timmytime.predictormessagereactive.service;

import com.timmytime.predictormessagereactive.facade.WebClientFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class TrainingTestService {

    private final WebClientFacade webClientFacade;

    public Mono<Void> trainTeams(
            @PathVariable  UUID receipt,
            @PathVariable String to,
            @PathVariable String from,
            @PathVariable String country){
        log.info("received training teams: {}", receipt);
        return Mono.empty();
    }

    public Mono<Void> trainPlayers(
            @PathVariable  UUID receipt,
            @PathVariable String to,
            @PathVariable String from){
        log.info("received training players: {}", receipt);
        return Mono.empty();
    }

}
