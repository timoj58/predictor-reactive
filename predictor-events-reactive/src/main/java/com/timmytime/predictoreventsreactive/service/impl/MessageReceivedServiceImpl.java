package com.timmytime.predictoreventsreactive.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictoreventsreactive.enumerator.CountryCompetitions;
import com.timmytime.predictoreventsreactive.enumerator.Messages;
import com.timmytime.predictoreventsreactive.facade.WebClientFacade;
import com.timmytime.predictoreventsreactive.request.Message;
import com.timmytime.predictoreventsreactive.service.*;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private final ValidationService validationService;
    private final WebClientFacade webClientFacade;
    private final PredictionMonitorService predictionMonitorService;

    private final String playersHost;
    private final Integer delay;

    @Autowired
    public MessageReceivedServiceImpl(
            @Value("${clients.players}") String playersHost,
            @Value("${delays.competition}") Integer delay,
            PredictionService predictionService,
            PredictionResultService predictionResultService,
            PredictionMonitorService predictionMonitorService,
            ValidationService validationService,
            WebClientFacade webClientFacade
    ) {
        this.playersHost = playersHost;
        this.delay = delay;
        this.predictionService = predictionService;
        this.predictionResultService = predictionResultService;
        this.predictionMonitorService = predictionMonitorService;
        this.validationService = validationService;
        this.webClientFacade = webClientFacade;

        Arrays.asList(CountryCompetitions.values())
                .forEach(country -> messages.put(country.name(), new ArrayList<>()));

    }

    @Override
    public Mono<Void> receive(Mono<Message> message) {
        return message.doOnNext(
                msg -> {
                    log.info("received {} {}", msg.getType(), msg.getCountry());
                    messages.get(msg.getCountry()).add(msg.getType());

                    if (messages.get(msg.getCountry()).containsAll(Arrays.asList(Messages.values()))) {

                        log.info("processing {}", msg.getCountry());
                        validationService.resetLast(msg.getCountry(), (country) ->
                                CompletableFuture.runAsync(() -> {
                                    log.info("starting predictions {}", country);
                                    validationService.validate(country);
                                    webClientFacade.sendMessage(playersHost + "/message", createMessage(country));
                                    predictionService.start(country);
                                }).thenRun(() ->
                                        Mono.just(country.toUpperCase())
                                                .delayElement(Duration.ofMinutes(delay))
                                                .subscribe(v -> predictionMonitorService.addCountry(CountryCompetitions.valueOf(v))))
                        );

                    }

                }
        ).thenEmpty(Mono.empty());

    }

    @Override
    public Mono<Void> prediction(UUID id, Mono<JsonNode> prediction) {
        return prediction.doOnNext(
                msg -> predictionResultService.result(id, new JSONObject(msg.toString()))
        ).thenEmpty(Mono.empty());
    }

    private JsonNode createMessage(String country) {
        try {
            return new ObjectMapper().readTree(
                    new JSONObject().put("country", country)
                            .put("type", "EVENTS_LOADED").toString()
            );
        } catch (JsonProcessingException e) {
            log.error("message failed", e);

            return null;
        }
    }
}
