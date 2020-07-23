package com.timmytime.predictorplayersreactive.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.timmytime.predictorplayersreactive.enumerator.ApplicableFantasyLeagues;
import com.timmytime.predictorplayersreactive.enumerator.Messages;
import com.timmytime.predictorplayersreactive.request.Message;
import com.timmytime.predictorplayersreactive.service.MessageReceivedService;
import com.timmytime.predictorplayersreactive.service.PlayersTrainingHistoryService;
import com.timmytime.predictorplayersreactive.service.PredictionService;
import com.timmytime.predictorplayersreactive.service.TrainingService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.*;

@Service("messageReceivedService")
public class MessageReceivedServiceImpl implements MessageReceivedService {

    private final Logger log = LoggerFactory.getLogger(MessageReceivedServiceImpl.class);
    private final Map<String, List<Messages>> messages = new HashMap<>();

    private final PredictionService predictionService;
    private final TrainingService trainingService;
    private final PlayersTrainingHistoryService playersTrainingHistoryService;

    @Autowired
    public MessageReceivedServiceImpl(
            PredictionService predictionService,
            TrainingService trainingService,
            PlayersTrainingHistoryService playersTrainingHistoryService
    ){
        this.predictionService = predictionService;
        this.trainingService = trainingService;
        this.playersTrainingHistoryService = playersTrainingHistoryService;

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
                    log.info("received {}", msg.getType());
                    messages.get(msg.getCountry().toLowerCase()).add(msg.getType());

                    if(messages.get(msg.getCountry().toLowerCase()).containsAll(Arrays.asList(Messages.values()))){
                        predictionService.start(msg.getCountry().toLowerCase());
                    }

                }
        ).thenEmpty(Mono.empty()); }

    @Override
    public Mono<Void> prediction(UUID id, Mono<JsonNode> prediction) {
        return prediction.doOnNext(
                msg ->  predictionService.result(id, new JSONObject(msg.toString()))
        ).thenEmpty(Mono.empty());

    }

    @Override
    public Mono<Void> training(UUID id) {
       return playersTrainingHistoryService.find(id)
               .doOnNext(history -> {
                   history.setCompleted(Boolean.TRUE);
                   playersTrainingHistoryService.save(history)
                           .subscribe(then -> {
                               if(then.getToDate().isBefore(LocalDate.now().atStartOfDay())){
                                   trainingService.train();
                               }else {
                                   log.info("training is complete");
                               }
                           });
               })
               .thenEmpty(Mono.empty());
    }

    @Override
    public Mono<Void> initTraining() {
        trainingService.train();
        return Mono.empty();
    }
}
