package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.enumerator.ApplicableFantasyLeagues;
import com.timmytime.predictorplayerseventsreactive.enumerator.Messages;
import com.timmytime.predictorplayerseventsreactive.request.Message;
import com.timmytime.predictorplayerseventsreactive.service.MessageReceivedService;
import com.timmytime.predictorplayerseventsreactive.service.PlayersTrainingHistoryService;
import com.timmytime.predictorplayerseventsreactive.service.TrainingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;

@Slf4j
@Service("messageReceivedService")
public class MessageReceivedServiceImpl implements MessageReceivedService {

    private final Map<String, List<Messages>> messages = new HashMap<>();

    private final TrainingService trainingService;
    private final PlayersTrainingHistoryService playersTrainingHistoryService;

    @Autowired
    public MessageReceivedServiceImpl(
            TrainingService trainingService,
            PlayersTrainingHistoryService playersTrainingHistoryService
    ) {
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
                    log.info("received {} {}", msg.getType(), msg.getCountry());
                    //TODO.  review if we need it?
                }
        ).thenEmpty(Mono.empty());
    }

    @Override
    public Mono<Void> training(UUID id) {
        return playersTrainingHistoryService.find(id)
                .doOnNext(history -> trainingService.train(history))
                .thenEmpty(Mono.empty());
    }

}
