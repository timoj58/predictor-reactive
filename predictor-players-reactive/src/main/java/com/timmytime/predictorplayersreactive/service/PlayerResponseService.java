package com.timmytime.predictorplayersreactive.service;

import com.timmytime.predictorplayersreactive.model.PlayerResponse;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PlayerResponseService {

    Mono<PlayerResponse> getPlayer(@PathVariable UUID id);
    void load(String country);
}
