package com.timmytime.predictorplayerseventsreactive.service;

import com.timmytime.predictorplayerseventsreactive.model.FantasyOutcome;
import com.timmytime.predictorplayerseventsreactive.model.PlayerResponse;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PlayerResponseService {

    Mono<PlayerResponse> getPlayer(@PathVariable UUID id);

    void addResult(FantasyOutcome fantasyOutcome);
}
