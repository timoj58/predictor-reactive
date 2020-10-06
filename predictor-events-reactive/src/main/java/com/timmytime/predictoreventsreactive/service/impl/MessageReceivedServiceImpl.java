package com.timmytime.predictoreventsreactive.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictoreventsreactive.enumerator.CountryCompetitions;
import com.timmytime.predictoreventsreactive.enumerator.Messages;
import com.timmytime.predictoreventsreactive.facade.WebClientFacade;
import com.timmytime.predictoreventsreactive.request.Message;
import com.timmytime.predictoreventsreactive.service.MessageReceivedService;
import com.timmytime.predictoreventsreactive.service.PredictionService;
import com.timmytime.predictoreventsreactive.service.ValidationService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;

@Service("messageReceivedService")
public class MessageReceivedServiceImpl implements MessageReceivedService {

    private static final Logger log = LoggerFactory.getLogger(MessageReceivedServiceImpl.class);
    private final Map<String, List<Messages>> messages = new HashMap<>();

    private final PredictionService predictionService;
    private final ValidationService validationService;
    private final WebClientFacade webClientFacade;

    private final String playersHost;

    @Autowired
    public MessageReceivedServiceImpl(
            @Value("${players.host}") String playersHost,
            PredictionService predictionService,
            ValidationService validationService,
            WebClientFacade webClientFacade
    ){
        this.playersHost = playersHost;
        this.predictionService = predictionService;
        this.validationService = validationService;
        this.webClientFacade = webClientFacade;

        Arrays.asList(CountryCompetitions.values())
                .stream()
                .forEach(country -> messages.put(country.name(), new ArrayList<>()));

    }

    @Override
    public Mono<Void> receive(Mono<Message> message) {
        return message.doOnNext(
                msg -> {
                    log.info("received {}", msg.getType());
                    messages.get(msg.getCountry()).add(msg.getType());

                    if(messages.get(msg.getCountry()).containsAll(Arrays.asList(Messages.values()))){

                        validationService.resetLast(msg.getCountry())
                        .subscribe(then -> {
                            validationService.validate(msg.getCountry());
                            webClientFacade.sendMessage(playersHost+"/message", createMessage(msg.getCountry()));
                            predictionService.start(msg.getCountry());
                        });

                    }

                }
        ).thenEmpty(Mono.empty());

    }

    @Override
    public Mono<Void> prediction(UUID id, Mono<JsonNode> prediction) {
        return prediction.doOnNext(
                msg ->  predictionService.result(id, new JSONObject(msg.toString()))
        ).thenEmpty(Mono.empty());
    }

    private JsonNode createMessage(String country){
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
