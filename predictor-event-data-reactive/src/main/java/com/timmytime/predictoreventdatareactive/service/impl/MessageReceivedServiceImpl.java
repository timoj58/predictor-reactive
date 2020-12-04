package com.timmytime.predictoreventdatareactive.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.timmytime.predictoreventdatareactive.enumerator.Providers;
import com.timmytime.predictoreventdatareactive.service.MessageReceivedService;
import com.timmytime.predictoreventdatareactive.service.ProviderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.function.Consumer;


@Service("messageReceivedService")
public class MessageReceivedServiceImpl implements MessageReceivedService {

    private final Logger log = LoggerFactory.getLogger(MessageReceivedServiceImpl.class);

    private final ProviderService betwayService;
    private final ProviderService paddyPowerService;

    private Consumer<JsonNode> receive;
    private final Flux<JsonNode> results;

    @Autowired
    public MessageReceivedServiceImpl(
            BetwayService betwayService,
            PaddyPowerService paddyPowerService
    ) {
        this.betwayService = betwayService;
        this.paddyPowerService = paddyPowerService;

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
        //shut down the scraper service with lambda call.
        return Mono.empty();
    }

    private void process(JsonNode received) {
        String provider = received.get("provider").textValue();
        log.info("received message from {}", provider);

        switch (Providers.valueOf(provider)) {
            case PADDYPOWER_ODDS:
                paddyPowerService.receive(received);
                break;
            case BETWAY_ODDS:
                betwayService.receive(received);
                break;
        }

    }


}
