package com.timmytime.predictorteamsreactive.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictorteamsreactive.enumerator.CountryCompetitions;
import com.timmytime.predictorteamsreactive.facade.WebClientFacade;
import com.timmytime.predictorteamsreactive.model.Message;
import com.timmytime.predictorteamsreactive.model.TrainingHistory;
import com.timmytime.predictorteamsreactive.repo.TrainingHistoryRepo;
import com.timmytime.predictorteamsreactive.service.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service("messageReceivedService")
public class MessageReceivedServiceImpl implements MessageReceivedService {

    private final List<String> received = new ArrayList<>();

    private final CompetitionService competitionService;
    private final TrainingHistoryService trainingHistoryService;
    private final TrainingService trainingService;
    private final WebClientFacade webClientFacade;

    private final String eventsHost;
    private final String playersHost;

    @Autowired
    public MessageReceivedServiceImpl(
            @Value("${events.host}") String eventsHost,
            @Value("${players.host}") String playersHost,
            CompetitionService competitionService,
            TrainingHistoryService trainingHistoryService,
            TrainingService trainingService,
            WebClientFacade webClientFacade
    ){
        this.eventsHost = eventsHost;
        this.playersHost = playersHost;
        this.competitionService = competitionService;
        this.trainingHistoryService = trainingHistoryService;
        this.trainingService = trainingService;
        this.webClientFacade = webClientFacade;
    }

    @Override
    public Mono<Void> receive(Mono<Message> message) {
        return message.doOnNext(
                msg -> {
                    received.add(msg.getCompetition());

                    if(received.containsAll(
                            CountryCompetitions.valueOf(msg.getCountry().toUpperCase()).getCompetitions()
                    )){
                        webClientFacade.sendMessage(
                                playersHost+"/message",
                                createMessage(msg.getCountry().toUpperCase(), "DATA_LOADED")
                        );
                        competitionService.load(trainingHistoryService.create(msg));
                    }
                }
        ).thenEmpty(Mono.empty());

    }

    @Override
    public Mono<Void> training(UUID id) {
        return Mono.just(
                trainingHistoryService.find(id)
        ).doOnNext(history -> {
            history.setCompleted(Boolean.TRUE);
            trainingHistoryService.save(history);

            //update -> this needs to advance training if we are not finished.  as per players.
            //as used by full training mode too....

            if(trainingHistoryService.finished()){
                    webClientFacade.sendMessage(
                            eventsHost+"/message",
                            createMessage(history.getCountry().toUpperCase(), "TRAINING_COMPLETED")
                    );

            }
        }).thenEmpty(Mono.empty());

    }

    @Override
    public Mono<Void> initTraining() {
        trainingService.train();
        return Mono.empty();
    }

    private JsonNode createMessage(String country, String type){
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
