package com.timmytime.predictoreventdatareactive.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.timmytime.predictoreventdatareactive.enumerator.Providers;
import com.timmytime.predictoreventdatareactive.service.MessageReceivedService;
import com.timmytime.predictoreventdatareactive.service.ProviderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.function.Consumer;


@Slf4j
@Service("messageReceivedService")
public class MessageReceivedServiceImpl implements MessageReceivedService {

    private final ProviderService providerService;
    private final Flux<JsonNode> results;
    private Consumer<JsonNode> receive;

    @Autowired
    public MessageReceivedServiceImpl(
            ProviderService providerService
    ) {
        this.providerService = providerService;

        this.results = Flux.push(sink ->
                MessageReceivedServiceImpl.this.receive = (t) -> sink.next(t), FluxSink.OverflowStrategy.BUFFER);

        this.results.limitRate(1).delayElements(Duration.ofMillis(500)).subscribe(this::process);
    }

    @Override
    public Mono<Void> receive(Mono<JsonNode> received) {
        return
                received.doOnNext(receive::accept)
                        .thenEmpty(Mono.empty());
    }

    @Override
    public Mono<Void> completed() {
        log.info("completed scrape message received");
        //shut down the scraper service with lambda call.  (not going to implement) TODO remove this
        return Mono.empty();
    }

    private void process(JsonNode received) {
        String provider = received.get("provider").textValue();
        log.info("received message from {}", provider);

        providerService.receive(Pair.of(Providers.valueOf(provider), received));

    }


}
