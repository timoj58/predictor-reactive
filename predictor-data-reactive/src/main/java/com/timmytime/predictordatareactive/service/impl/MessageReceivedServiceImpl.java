package com.timmytime.predictordatareactive.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.timmytime.predictordatareactive.service.MessageReceivedService;
import com.timmytime.predictordatareactive.service.ResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;


@Slf4j
@Service("messageReceivedService")
public class MessageReceivedServiceImpl implements MessageReceivedService {


    private final ResultService resultService;
    private Consumer<JsonNode> receive;

    @Autowired
    public MessageReceivedServiceImpl(
            ResultService resultService
    ) {
        this.resultService = resultService;

        Flux<JsonNode> results = Flux.push(sink ->
                MessageReceivedServiceImpl.this.receive = sink::next, FluxSink.OverflowStrategy.BUFFER);

        results.subscribe(this::process);
    }

    @Override
    public Mono<Void> receive(Mono<JsonNode> received) {
        return received.doOnNext(receive)
                .thenEmpty(Mono.empty());
    }

    @Override
    public Mono<Void> completed() {
        log.info("completed scrape message received");
        //shut down the scraper service with lambda call.
        return Mono.empty();
    }

    private void process(JsonNode received) {

        Integer matchId = received.get("matchId").asInt();
        String type = received.get("type").textValue();
        log.info("match: {} received message {}", matchId, received.toString());

        resultService.findByMatch(matchId)
                .subscribe(result -> {
                    log.info("we have a record {}", result.getMatchId());
                    switch (type) {
                        case "result":
                            result.setResult(received.toString());
                            break;
                        case "lineup":
                            result.setLineup(received.toString());
                            break;
                        case "match":
                            result.setMatch(received.toString());
                            break;
                    }

                    resultService.process(result);
                });

    }

}
