package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayerseventsreactive.model.PlayersTrainingHistory;
import com.timmytime.predictorplayerseventsreactive.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service("trainingService")
public class TrainingServiceImpl implements TrainingService {

    private final PlayerService playerService;
    private final PlayersTrainingHistoryService playersTrainingHistoryService;
    private final TensorflowTrainingService tensorflowTrainingService;
    private final PlayerMatchService playerMatchService;
    private final TensorflowDataService tensorflowDataService;

    private final Integer interval;
    private final Integer playerDelay;

    private final FantasyEventTypes first;
    private final List<FantasyEventTypes> toTrain;

    @Autowired
    public TrainingServiceImpl(
            @Value("${training.interval}") Integer interval,
            @Value("${training.player-delay}") Integer playerDelay,
            PlayerService playerService,
            PlayersTrainingHistoryService playersTrainingHistoryService,
            TensorflowTrainingService tensorflowTrainingService,
            PlayerMatchService playerMatchService,
            TensorflowDataService tensorflowDataService
    ) {
        this.interval = interval;
        this.playerDelay = playerDelay;
        this.playerService = playerService;
        this.playersTrainingHistoryService = playersTrainingHistoryService;
        this.tensorflowTrainingService = tensorflowTrainingService;
        this.playerMatchService = playerMatchService;
        this.tensorflowDataService = tensorflowDataService;

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
                                        history.getToDate().plusYears(interval)
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
                    if (playersTrainingHistory.getToDate().isBefore(LocalDate.now().atStartOfDay())) {
                        train(history.getType());
                    } else {
                        log.info("training is complete for {}", playersTrainingHistory.getType());
                        //need to start the next item available in list...
                        if (!toTrain.isEmpty()) {
                            FantasyEventTypes next = toTrain.stream().findFirst().get();
                            log.info("starting {}", next);
                            train(next);
                            toTrain.remove(next);
                        } else {
                            log.info("training is complete"); //we only train off-line not in realtime.
                        }
                    }
                });

    }

    @Override
    public void train() {

        //WARNING:  do not run this again.  need to review regarding starting training from the last point.

        var players = playerService.get();
        log.info("processing {} players", players.size());
        CompletableFuture.runAsync(tensorflowDataService::delete) //note.  probably better not to delete it all.  given size of dataset. (and storage cost)
                .thenRun(() ->
                        Flux.fromStream(players.stream())
                                .limitRate(1)
                                .delayElements(Duration.ofMillis(playerDelay)) //maybe not needed.
                                .doOnNext(player -> playerMatchService.create(
                                        player.getId(),
                                        tensorflowDataService::load))
                                .doFinally(train -> playersTrainingHistoryService.find(first).subscribe(this::train)
                                )
                                .subscribe()
                );
    }

    @PostConstruct
    private void init() {

        Arrays.stream(FantasyEventTypes.values())
                .filter(f -> f.getPredict() == Boolean.TRUE)
                .forEach(type ->
                        playersTrainingHistoryService.findOptional(type)
                                .ifPresentOrElse(then -> log.info("we have history"),
                                        () -> {
                                            log.info("init record");
                                            var history = new PlayersTrainingHistory(
                                                    type,
                                                    LocalDate.parse("01-08-2009", DateTimeFormatter.ofPattern("dd-MM-yyyy")).atStartOfDay(),
                                                    LocalDate.parse("01-08-2009", DateTimeFormatter.ofPattern("dd-MM-yyyy")).atStartOfDay()
                                            );

                                            history.setCompleted(Boolean.TRUE);
                                            playersTrainingHistoryService.saveNormal(history);

                                        })

                );

    }

}
