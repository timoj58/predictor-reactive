package com.timmytime.predictorplayersreactive.service.impl;

import com.timmytime.predictorplayersreactive.request.Message;
import com.timmytime.predictorplayersreactive.service.MessageReceivedService;
import com.timmytime.predictorplayersreactive.service.PlayersTrainingHistoryService;
import com.timmytime.predictorplayersreactive.service.TrainingModelService;
import com.timmytime.predictorplayersreactive.service.TrainingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service("messageReceivedService")
public class MessageReceivedServiceImpl implements MessageReceivedService {

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
                msg -> playersTrainingHistoryService.find(trainingService.firstTrainingEvent())
                        .subscribe(trainingModelService::next)
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
