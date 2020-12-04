package com.timmytime.predictorclientreactive.service;

import com.timmytime.predictorclientreactive.request.Message;
import reactor.core.publisher.Mono;

public interface MessageReceivedService {
    Mono<Void> receive(Mono<Message> message);

    Mono<Void> test();

}
