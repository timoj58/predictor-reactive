package com.timmytime.predictorplayersreactive.service;

import com.timmytime.predictorplayersreactive.model.PlayerMatch;
import reactor.core.publisher.Flux;

public interface PlayerMatchService {
    Flux<PlayerMatch> get(String fromDate, String toDate);
}
