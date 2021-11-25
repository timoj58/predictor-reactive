package com.timmytime.predictormessagereactive.service;

import com.timmytime.predictormessagereactive.request.Message;
import reactor.core.publisher.Mono;

public interface MessageReceivedService {
    Mono<Void> receive(Mono<Message> message);
}
