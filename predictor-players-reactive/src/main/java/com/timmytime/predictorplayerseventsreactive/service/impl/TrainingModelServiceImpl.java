package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.model.PlayersTrainingHistory;
import com.timmytime.predictorplayerseventsreactive.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service("trainingModelService")
public class TrainingModelServiceImpl implements TrainingModelService {

    private final PlayerService playerService;
    private final PlayersTrainingHistoryService playersTrainingHistoryService;
    private final PlayerMatchService playerMatchService;
    private final TrainingService trainingService;
    private final TensorflowDataService tensorflowDataService;


    @Autowired
    public TrainingModelServiceImpl(
            PlayerService playerService,
            PlayersTrainingHistoryService playersTrainingHistoryService,
            PlayerMatchService playerMatchService,
            TrainingService trainingService,
            TensorflowDataService tensorflowDataService
    ) {
        this.playerService = playerService;
        this.playersTrainingHistoryService = playersTrainingHistoryService;
        this.playerMatchService = playerMatchService;
        this.trainingService = trainingService;
        this.tensorflowDataService = tensorflowDataService;
    }


    @Override
    public void create() {

        var players = playerService.get().stream()
                .filter(f -> f.getLastAppearance().atStartOfDay().isAfter(LocalDateTime.now().minusYears(2)))
                .collect(Collectors.toList());

        log.info("processing {} players active in last two years", players.size());
        Flux.fromStream(players.stream())
                .limitRate(1)
                .delayElements(Duration.ofSeconds(2))
                .doOnNext(player -> playerMatchService.create(
                        player.getId(),
                        tensorflowDataService::load))
                .doFinally(this::startTraining)
                .subscribe();
    }

    @Override
    public void next(PlayersTrainingHistory playersTrainingHistory) {
        var players = playerService.get().stream()
                .filter(f -> f.getLastAppearance().atStartOfDay().isAfter(LocalDateTime.now().minusYears(2)))
                .collect(Collectors.toList());

        log.info("processing {} players", players.size());
        CompletableFuture.runAsync(() ->
                Flux.fromStream(players.stream())
                        .limitRate(1)
                        .delayElements(Duration.ofSeconds(2))
                        .doOnNext(player -> playerMatchService.next(
                                player.getId(),
                                playersTrainingHistory.getFromDate().toLocalDate(),
                                tensorflowDataService::load)
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
