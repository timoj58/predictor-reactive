package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.timmytime.predictorplayerseventsreactive.enumerator.ApplicableFantasyLeagues;
import com.timmytime.predictorplayerseventsreactive.enumerator.Messages;
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
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service("messageReceivedService")
public class MessageReceivedServiceImpl implements MessageReceivedService {

    private final Map<String, List<Messages>> messages = new HashMap<>();

    private final PredictionService predictionService;
    private final PredictionResultService predictionResultService;
    private final PredictionMonitorService predictionMonitorService;

    @Autowired
    public MessageReceivedServiceImpl(
            PredictionService predictionService,
            PredictionResultService predictionResultService,
            PredictionMonitorService predictionMonitorService
    ) {
        this.predictionService = predictionService;
        this.predictionResultService = predictionResultService;
        this.predictionMonitorService = predictionMonitorService;

        Arrays.asList(
                ApplicableFantasyLeagues.values()
        ).stream()
                .map(ApplicableFantasyLeagues::getCountry)
                .distinct()
                .forEach(country -> messages.put(country.toLowerCase(), new ArrayList<>()));

    }

    @Override
    public Mono<Void> receive(Mono<Message> message) {

        return message.doOnNext(
                msg -> {
                    log.info("received {} {}", msg.getType(), msg.getCountry());
                    if (messages.containsKey(msg.getCountry().toLowerCase())) {
                        messages.get(msg.getCountry().toLowerCase()).add(msg.getType());

                        if (messages.get(msg.getCountry().toLowerCase()).containsAll(Arrays.asList(Messages.values()))) {
                            CompletableFuture.runAsync(() -> predictionService.start(msg.getCountry().toLowerCase()))
                                    .thenRun(() -> Mono.just(msg.getCountry().toLowerCase())
                                            .delayElement(Duration.ofMinutes(1))
                                            .subscribe(v -> predictionMonitorService.addCountry(v))
                                    );
                        }
                    } else {
                        log.info("skipping {} not applicable", msg.getCountry());
                    }

                }
        ).thenEmpty(Mono.empty());
    }

    @Override
    public Mono<Void> prediction(UUID id, Mono<JsonNode> prediction) {
        log.info("receiving prediction result for {}", id);
        return prediction.doOnNext(
                msg -> predictionResultService.result(id, new JSONObject(msg.toString()), c -> predictionService.reProcess())
        ).thenEmpty(Mono.empty());

    }

}
