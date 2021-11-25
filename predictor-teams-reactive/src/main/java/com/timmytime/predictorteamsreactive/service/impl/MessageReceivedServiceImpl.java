package com.timmytime.predictorteamsreactive.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictorteamsreactive.enumerator.CountryCompetitions;
import com.timmytime.predictorteamsreactive.enumerator.Training;
import com.timmytime.predictorteamsreactive.facade.WebClientFacade;
import com.timmytime.predictorteamsreactive.model.Message;
import com.timmytime.predictorteamsreactive.model.TrainingHistory;
import com.timmytime.predictorteamsreactive.service.*;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service("messageReceivedService")
public class MessageReceivedServiceImpl implements MessageReceivedService {

    private final TrainingHistoryService trainingHistoryService;
    private final TrainingService trainingService;
    private final TrainingModelService trainingModelService;
    private final TensorflowDataService tensorflowDataService;
    private final WebClientFacade webClientFacade;
    private final String messageHost;
    private final Boolean trainingEvaluation;


    @Autowired
    public MessageReceivedServiceImpl(
            @Value("${clients.message}") String messageHost,
            @Value("${training.evaluation}") Boolean trainingEvaluation,
            TrainingHistoryService trainingHistoryService,
            TrainingModelService trainingModelService,
            TrainingService trainingService,
            TensorflowDataService tensorflowDataService,
            WebClientFacade webClientFacade
    ) {
        this.messageHost = messageHost;
        this.trainingEvaluation = trainingEvaluation;
        this.trainingHistoryService = trainingHistoryService;
        this.trainingModelService = trainingModelService;
        this.trainingService = trainingService;
        this.tensorflowDataService = tensorflowDataService;
        this.webClientFacade = webClientFacade;
    }

    @Override
    public Mono<Void> receive(Mono<Message> message) {

        /*
          TODO need to fix this into a pipeline, so only one country trains at a time.

          or do it in message service, but sort of pointless.
         */

        return message.doOnNext(
                msg ->
                        tensorflowDataService.loadOutstanding(msg.getCountry().toLowerCase(), () ->
                                trainingService.train(i -> {
                                    var trainingHistory = trainingHistoryService.find(Training.TRAIN_RESULTS, msg.getCountry());
                                    return trainingHistoryService.save(
                                            new TrainingHistory(
                                                    trainingHistory.getType(),
                                                    trainingHistory.getCountry(),
                                                    trainingHistory.getToDate(),
                                                    trainingHistory.getToDate().plusYears(i).isAfter(LocalDateTime.now()) ?
                                                            LocalDateTime.now() : trainingHistory.getToDate().plusYears(i))
                                    );
                                })
                        )
        ).thenEmpty(Mono.empty());

    }

    @Override
    public Mono<Void> training(UUID id) {

        log.info("received {}", id);

        return Mono.just(
                trainingHistoryService.find(id)
        ).doOnNext(history -> {
            if (history.getToDate().isAfter(LocalDate.now().atStartOfDay())) {
                trainingHistoryService.completeTraining(history);
                switch (history.getType()) {
                    case TRAIN_RESULTS:
                        //we start training goals..
                        log.info("now training goals for {}", history.getCountry());
                        trainingService.train(i -> trainingHistoryService.next(Training.TRAIN_GOALS, history.getCountry(), i));
                        break;
                    case TRAIN_GOALS:
                        log.info("finishing up {}", history.getCountry().toUpperCase());
                        //finished.
                        tensorflowDataService.clear(history.getCountry());
                        if (trainingEvaluation) {
                            trainingModelService.create();
                        } else {
                            webClientFacade.sendMessage(
                                    messageHost + "/message",
                                    createMessage(history.getCountry().toUpperCase(), "TRAINING_COMPLETED")
                            );
                        }

                        break;
                }
            } else {
                trainingService.train(i -> trainingHistoryService.save(
                        new TrainingHistory(
                                history.getType(),
                                history.getCountry(),
                                history.getToDate(),
                                history.getToDate().plusYears(i).isAfter(LocalDateTime.now()) ?
                                        LocalDateTime.now() : history.getToDate().plusYears(i)
                        )
                ));
            }
        }).thenEmpty(Mono.empty());

    }

    @Override
    public Mono<Void> createTrainingModels() {
        trainingModelService.create();
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
