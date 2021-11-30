package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.timmytime.predictorplayerseventsreactive.enumerator.ApplicableFantasyLeagues;
import com.timmytime.predictorplayerseventsreactive.request.Message;
import com.timmytime.predictorplayerseventsreactive.service.MessageReceivedService;
import com.timmytime.predictorplayerseventsreactive.service.PredictionMonitorService;
import com.timmytime.predictorplayerseventsreactive.service.PredictionResultService;
import com.timmytime.predictorplayerseventsreactive.service.PredictionService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service("messageReceivedService")
public class MessageReceivedServiceImpl implements MessageReceivedService {

    private final PredictionService predictionService;
    private final PredictionResultService predictionResultService;

    @Autowired
    public MessageReceivedServiceImpl(
            PredictionService predictionService,
            PredictionResultService predictionResultService
    ) {
        this.predictionService = predictionService;
        this.predictionResultService = predictionResultService;
    }

    @Override
    public Mono<Void> receive(Mono<Message> message) {

        return message.doOnNext(
                msg -> {
                    log.info("received {}", msg.getEventType());
                    if (ApplicableFantasyLeagues.getCountries().contains(msg.getEventType().toLowerCase())) {
                        CompletableFuture.runAsync(() -> predictionService.start(msg.getEventType().toLowerCase()));
                    }
                }
        ).thenEmpty(Mono.empty());
    }

    @Override
    public Mono<Void> prediction(UUID id, Mono<JsonNode> prediction) {
        log.info("receiving prediction result for {}", id);
        return prediction.doOnNext(
                msg -> predictionResultService.result(id, new JSONObject(msg.toString()))
        ).thenEmpty(Mono.empty());

    }

}
