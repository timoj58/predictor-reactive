package com.timmytime.predictoreventsreactive.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.timmytime.predictoreventsreactive.enumerator.CountryCompetitions;
import com.timmytime.predictoreventsreactive.request.Message;
import com.timmytime.predictoreventsreactive.service.*;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service("messageReceivedService")
public class MessageReceivedServiceImpl implements MessageReceivedService {

    private final PredictionService predictionService;
    private final PredictionResultService predictionResultService;
    private final ValidationService validationService;


    @Autowired
    public MessageReceivedServiceImpl(
            PredictionService predictionService,
            PredictionResultService predictionResultService,
            ValidationService validationService
    ) {
        this.predictionService = predictionService;
        this.predictionResultService = predictionResultService;
        this.validationService = validationService;

    }

    @Override
    public Mono<Void> receive(Mono<Message> message) {
        return message.doOnNext(
                msg -> {
                    log.info("processing {}", msg.getEventType());
                    validationService.resetLast(msg.getEventType(), (country) ->
                            CompletableFuture.runAsync(() -> {
                                log.info("starting predictions {}", country);
                                validationService.validate(country);
                                predictionService.start(country);
                            }));

                }

        ).thenEmpty(Mono.empty());

    }

    @Override
    public Mono<Void> prediction(UUID id, Mono<JsonNode> prediction) {
        return prediction.doOnNext(
                msg -> predictionResultService.result(id, new JSONObject(msg.toString()))
        ).thenEmpty(Mono.empty());
    }

}
