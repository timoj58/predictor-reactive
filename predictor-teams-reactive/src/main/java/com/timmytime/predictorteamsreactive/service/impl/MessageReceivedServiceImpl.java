package com.timmytime.predictorteamsreactive.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictorteamsreactive.enumerator.CountryCompetitions;
import com.timmytime.predictorteamsreactive.enumerator.Training;
import com.timmytime.predictorteamsreactive.facade.WebClientFacade;
import com.timmytime.predictorteamsreactive.model.Message;
import com.timmytime.predictorteamsreactive.service.MessageReceivedService;
import com.timmytime.predictorteamsreactive.service.TensorflowDataService;
import com.timmytime.predictorteamsreactive.service.TrainingHistoryService;
import com.timmytime.predictorteamsreactive.service.TrainingService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service("messageReceivedService")
public class MessageReceivedServiceImpl implements MessageReceivedService {

    private final List<String> received = new ArrayList<>();

    private final TrainingHistoryService trainingHistoryService;
    private final TrainingService trainingService;
    private final TensorflowDataService tensorflowDataService;
    private final WebClientFacade webClientFacade;
    private final String eventsHost;
    private final String playersHost;

    @Autowired
    public MessageReceivedServiceImpl(
            @Value("${clients.events}") String eventsHost,
            @Value("${clients.players}") String playersHost,
            TrainingHistoryService trainingHistoryService,
            TrainingService trainingService,
            TensorflowDataService tensorflowDataService,
            WebClientFacade webClientFacade
    ) {
        this.eventsHost = eventsHost;
        this.playersHost = playersHost;
        this.trainingHistoryService = trainingHistoryService;
        this.trainingService = trainingService;
        this.tensorflowDataService = tensorflowDataService;
        this.webClientFacade = webClientFacade;
    }

    @Override
    public Mono<Void> receive(Mono<Message> message) {
        return message.doOnNext(
                msg -> {
                    received.add(msg.getCompetition());

                    if (received.containsAll(
                            CountryCompetitions.valueOf(msg.getCountry().toUpperCase()).getCompetitions()
                    )) {
                        webClientFacade.sendMessage(
                                playersHost + "/message",
                                createMessage(msg.getCountry().toUpperCase(), "DATA_LOADED")
                        );
                        trainingService.train(trainingHistoryService.find(Training.TRAIN_RESULTS, msg.getCountry()), Boolean.FALSE);
                    }
                }
        ).thenEmpty(Mono.empty());

    }

    @Override
    public Mono<Void> training(UUID id) {

        log.info("received {}", id);

        return Mono.just(
                trainingHistoryService.find(id)
        ).doOnNext(history -> {
            if (!trainingService.train(history, Boolean.FALSE)) {
                switch (history.getType()) {
                    case TRAIN_RESULTS:
                        //we start training goals..
                        log.info("now training goals for {}", history.getCountry());
                        trainingService.train(
                                trainingService.init(Training.TRAIN_GOALS, history.getCountry()),
                                Boolean.TRUE
                        );
                        break;
                    case TRAIN_GOALS:
                        log.info("finishing up {}", history.getCountry().toUpperCase());
                        //finished.
                        tensorflowDataService.clear(history.getCountry());
                        webClientFacade.sendMessage(
                                eventsHost + "/message",
                                createMessage(history.getCountry().toUpperCase(), "TRAINING_COMPLETED")
                        );
                        break;
                }
            }
        }).thenEmpty(Mono.empty());

    }

    @Override
    public Mono<Void> initTraining() {
        trainingService.train();
        return Mono.empty();
    }

    private JsonNode createMessage(String country, String type) {
        try {
            return new ObjectMapper().readTree(
                    new JSONObject()
                            .put("type", type)
                            .put("country", country)
                            .toString()
            );
        } catch (JsonProcessingException e) {
            return null;
        }
    }

}
