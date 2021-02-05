package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.model.PlayersTrainingHistory;
import com.timmytime.predictorplayerseventsreactive.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service("trainingModelService")
public class TrainingModelServiceImpl implements TrainingModelService {

    private final PlayerService playerService;
    private final PlayersTrainingHistoryService playersTrainingHistoryService;
    private final PlayerMatchService playerMatchService;
    private final TrainingService trainingService;
    private final TensorflowDataService tensorflowDataService;

    private final Integer playerDelay;
    private final Boolean deleteMode;


    @Autowired
    public TrainingModelServiceImpl(
            @Value("${training.player-delay}") Integer playerDelay,
            @Value("${training.delete-mode}") Boolean deleteMode,
            PlayerService playerService,
            PlayersTrainingHistoryService playersTrainingHistoryService,
            PlayerMatchService playerMatchService,
            TrainingService trainingService,
            TensorflowDataService tensorflowDataService
    ) {
        this.playerDelay = playerDelay;
        this.deleteMode = deleteMode;
        this.playerService = playerService;
        this.playersTrainingHistoryService = playersTrainingHistoryService;
        this.playerMatchService = playerMatchService;
        this.trainingService = trainingService;
        this.tensorflowDataService = tensorflowDataService;
    }


    @Override
    public void create() {

        var players = playerService.get();
        log.info("processing {} players", players.size());
        CompletableFuture.runAsync(() -> {
            if (deleteMode  == Boolean.TRUE) {
                tensorflowDataService.delete();
            }
        })
                .thenRun(() ->
                        Flux.fromStream(players.stream())
                                .limitRate(1)
                                .delayElements(Duration.ofMillis(playerDelay)) //maybe not needed.
                                .doOnNext(player -> playerMatchService.create(
                                        player.getId(),
                                        tensorflowDataService::load))
                                .doFinally(this::startTraining)
                                .subscribe()
                );
    }

    @Override
    public void next(PlayersTrainingHistory playersTrainingHistory) {
        var players = playerService.get();
        log.info("processing {} players", players.size());
        CompletableFuture.runAsync(() ->
                Flux.fromStream(players.stream())
                        .limitRate(1)
                        .delayElements(Duration.ofMillis(playerDelay))
                        .doOnNext(player -> playerMatchService.create(
                                player.getId(),
                                playerMatch -> {
                                    if (playerMatch.getDate().atStartOfDay().isAfter(playersTrainingHistory.getFromDate())) {
                                        tensorflowDataService.load(playerMatch);
                                    }
                                })
                        )
                        .doFinally(this::startTraining)
                        .subscribe()
        );

    }

    private void startTraining(SignalType signalType) {
        playersTrainingHistoryService.find(
                trainingService.firstTrainingEvent()
        ).subscribe(trainingService::train);
    }

}
