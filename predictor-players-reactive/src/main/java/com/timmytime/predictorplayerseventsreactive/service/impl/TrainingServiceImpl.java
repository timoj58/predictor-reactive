package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayerseventsreactive.model.Player;
import com.timmytime.predictorplayerseventsreactive.model.PlayersTrainingHistory;
import com.timmytime.predictorplayerseventsreactive.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
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

        toTrain = Arrays.asList(
                FantasyEventTypes.values()
        ).stream().filter(f -> f.getPredict() == Boolean.TRUE)
                .collect(Collectors.toList());

        first = toTrain.stream().findFirst().get();
        toTrain.remove(first);

    }


    @Override
    public void train(FantasyEventTypes type) {

        List<Player> players = playerService.get();
        Integer playerCount = players.size();

        log.info("training init {} for {} players", type.name(), playerCount);

        playersTrainingHistoryService.find(type)
                .doOnNext(history ->
                        playersTrainingHistoryService.save(
                                new PlayersTrainingHistory(
                                        history.getType(),
                                        history.getToDate(),
                                        history.getToDate().plusYears(interval)
                                )
                        ).subscribe(trainingHistory -> {
                            if (type.equals(first)) {
                                String fromDate = trainingHistory.getFromDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                                String toDate = trainingHistory.getToDate().plusYears(1).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                                Flux.fromStream(
                                        players.stream()
                                ).limitRate(100)
                                        .delayElements(Duration.ofMillis(playerDelay))
                                        .doOnNext(player -> playerMatchService.create(
                                                player.getId(),
                                                fromDate,
                                                toDate,
                                                (data) -> tensorflowDataService.load(data)))
                                        .doFinally(train ->
                                                Mono.just(trainingHistory.getId())
                                                        .delayElement(Duration.ofMinutes(interval))
                                                        .subscribe(id -> tensorflowTrainingService.train(id))
                                        ).subscribe();
                            } else {
                                log.info("training without loading data {}", type.name());
                                tensorflowTrainingService.train(trainingHistory.getId());
                            }
                        })
                )
                .subscribe();

    }

    @Override
    public void train(PlayersTrainingHistory playersTrainingHistory) {
        playersTrainingHistory.setCompleted(Boolean.TRUE);
        playersTrainingHistoryService.save(playersTrainingHistory)
                .subscribe(history -> {
                    tensorflowDataService.clear();
                    if (playersTrainingHistory.getToDate().isBefore(LocalDate.now().atStartOfDay())) {
                        train(history.getType());
                    } else {
                        log.info("training is complete for {}", playersTrainingHistory.getType());
                        //need to start the next item available in list...
                        if (!toTrain.isEmpty()) {
                            FantasyEventTypes next = toTrain.stream().findFirst().get();
                            toTrain.remove(next);
                            train(next);
                        } else {
                            log.info("training is complete"); //we only train off-line not in realtime.
                        }
                    }
                });

    }

    @PostConstruct
    private void init() {

        Flux.fromStream(
                Arrays.asList(FantasyEventTypes.values())
                        .stream()
                        .filter(f -> f.getPredict() == Boolean.TRUE)
        ).subscribe(type ->
                playersTrainingHistoryService.find(type)
                        .switchIfEmpty(Mono.just(new PlayersTrainingHistory()))
                        .subscribe(history -> {

                            if (history.getId() == null) {
                                log.info("init record");
                                history = new PlayersTrainingHistory(
                                        type,
                                        LocalDate.parse("01-08-2009", DateTimeFormatter.ofPattern("dd-MM-yyyy")).atStartOfDay(),
                                        LocalDate.parse("01-08-2009", DateTimeFormatter.ofPattern("dd-MM-yyyy")).atStartOfDay()
                                );

                                history.setCompleted(Boolean.TRUE);
                                playersTrainingHistoryService.save(history).subscribe();
                            }

                        })
        );

    }

}
