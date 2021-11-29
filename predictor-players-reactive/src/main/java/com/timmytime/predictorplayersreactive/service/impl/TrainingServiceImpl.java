package com.timmytime.predictorplayersreactive.service.impl;

import com.timmytime.predictorplayersreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayersreactive.facade.WebClientFacade;
import com.timmytime.predictorplayersreactive.model.PlayersTrainingHistory;
import com.timmytime.predictorplayersreactive.request.Message;
import com.timmytime.predictorplayersreactive.service.PlayersTrainingHistoryService;
import com.timmytime.predictorplayersreactive.service.TensorflowTrainingService;
import com.timmytime.predictorplayersreactive.service.TrainingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service("trainingService")
public class TrainingServiceImpl implements TrainingService {

    private final PlayersTrainingHistoryService playersTrainingHistoryService;
    private final TensorflowTrainingService tensorflowTrainingService;
    private final WebClientFacade webClientFacade;

    private final Integer interval;
    private final String messageHost;

    private final FantasyEventTypes first;
    private final List<FantasyEventTypes> toTrain;

    @Autowired
    public TrainingServiceImpl(
            @Value("${training.interval}") Integer interval,
            @Value("${clients.message}") String messageHost,
            PlayersTrainingHistoryService playersTrainingHistoryService,
            TensorflowTrainingService tensorflowTrainingService,
            WebClientFacade webClientFacade
    ) {
        this.interval = interval;
        this.messageHost = messageHost;
        this.playersTrainingHistoryService = playersTrainingHistoryService;
        this.tensorflowTrainingService = tensorflowTrainingService;
        this.webClientFacade = webClientFacade;

        toTrain = Arrays.stream(
                FantasyEventTypes.values()
        ).filter(f -> f.getPredict() == Boolean.TRUE)
                .collect(Collectors.toList());

        first = toTrain.stream().findFirst().get();
        toTrain.remove(first);
    }


    @Override
    public void train(FantasyEventTypes type) {

        playersTrainingHistoryService.find(type)
                .doOnNext(history ->
                        playersTrainingHistoryService.save(
                                new PlayersTrainingHistory(
                                        history.getType(),
                                        history.getToDate(),
                                        history.getToDate().plusYears(interval).isAfter(LocalDateTime.now()) ?
                                                LocalDateTime.now() : history.getToDate().plusYears(interval)
                                )
                        ).subscribe(trainingHistory -> tensorflowTrainingService.train(trainingHistory.getId()))
                )
                .subscribe();

    }

    @Override
    public void train(PlayersTrainingHistory playersTrainingHistory) {
        log.info("training {}", playersTrainingHistory.getType().name());
        playersTrainingHistory.setCompleted(Boolean.TRUE);
        playersTrainingHistoryService.save(playersTrainingHistory)
                .subscribe(history -> {
                    if (history.getToDate().isBefore(LocalDate.now().atStartOfDay())) {
                        train(history.getType());
                    } else {
                        log.info("training is complete for {}", history.getType());
                        //need to start the next item available in list...
                        if (!toTrain.isEmpty()) {
                            FantasyEventTypes next = toTrain.stream().findFirst().get();
                            log.info("starting {}", next);
                            train(next);
                            toTrain.remove(next);
                        } else {
                            log.info("training is complete");
                            webClientFacade.sendMessage(
                                    messageHost+"/message",
                                    Message.builder().event("PLAYERS_TRAINED").eventType("ALL").build()

                            );
                        }
                    }
                });

    }

    @Override
    public FantasyEventTypes firstTrainingEvent() {
        return first;
    }

}
