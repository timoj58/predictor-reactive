package com.timmytime.predictordatareactive.service;

import com.timmytime.predictordatareactive.model.Result;
import reactor.core.publisher.Mono;

public interface ResultService {

    void process(Result result);

    Mono<Result> findByMatch(Integer matchId);

    void repair();

    void init();
}
