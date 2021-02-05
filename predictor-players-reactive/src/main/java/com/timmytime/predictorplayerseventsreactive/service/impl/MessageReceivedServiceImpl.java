package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.enumerator.ApplicableFantasyLeagues;
import com.timmytime.predictorplayerseventsreactive.request.Message;
import com.timmytime.predictorplayerseventsreactive.service.MessageReceivedService;
import com.timmytime.predictorplayerseventsreactive.service.PlayersTrainingHistoryService;
import com.timmytime.predictorplayerseventsreactive.service.TrainingModelService;
import com.timmytime.predictorplayerseventsreactive.service.TrainingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service("messageReceivedService")
public class MessageReceivedServiceImpl implements MessageReceivedService {

    private final List<ApplicableFantasyLeagues> messages = new ArrayList<>();

    private final TrainingService trainingService;
    private final TrainingModelService trainingModelService;
    private final PlayersTrainingHistoryService playersTrainingHistoryService;

    @Autowired
    public MessageReceivedServiceImpl(
            TrainingService trainingService,
            TrainingModelService trainingModelService,
            PlayersTrainingHistoryService playersTrainingHistoryService
    ) {
        this.trainingService = trainingService;
        this.trainingModelService = trainingModelService;
        this.playersTrainingHistoryService = playersTrainingHistoryService;

    }

    @Override
    public Mono<Void> receive(Mono<Message> message) {

        return message.doOnNext(
                msg -> {
                    log.info("received {} {}", msg.getCountry(), msg.getCompetition());
                    messages.add(ApplicableFantasyLeagues.valueOf(msg.getCompetition().toUpperCase()));
                    if (messages.containsAll(Arrays.asList(ApplicableFantasyLeagues.values()))) {
                        log.info("start player training");
                        playersTrainingHistoryService.find(trainingService.firstTrainingEvent())
                                .subscribe(trainingModelService::next);
                        //TODO note we now need to send a message to player events once completed
                    }
                }
        ).thenEmpty(Mono.empty());
    }

    @Override
    public Mono<Void> training(UUID id) {
        return playersTrainingHistoryService.find(id)
                .doOnNext(trainingService::train)
                .thenEmpty(Mono.empty());
    }

    @Override
    public Mono<Void> createTrainingModel() {
        CompletableFuture.runAsync(trainingModelService::create);
        return Mono.empty();
    }

}
