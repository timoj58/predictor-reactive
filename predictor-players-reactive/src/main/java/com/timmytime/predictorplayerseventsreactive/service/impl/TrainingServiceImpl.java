package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayerseventsreactive.model.PlayersTrainingHistory;
import com.timmytime.predictorplayerseventsreactive.service.PlayersTrainingHistoryService;
import com.timmytime.predictorplayerseventsreactive.service.TensorflowTrainingService;
import com.timmytime.predictorplayerseventsreactive.service.TrainingService;
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

    private final Integer interval;

    private final FantasyEventTypes first;
    private final List<FantasyEventTypes> toTrain;

    @Autowired
    public TrainingServiceImpl(
            @Value("${training.interval}") Integer interval,
            PlayersTrainingHistoryService playersTrainingHistoryService,
            TensorflowTrainingService tensorflowTrainingService
    ) {
        this.interval = interval;
        this.playersTrainingHistoryService = playersTrainingHistoryService;
        this.tensorflowTrainingService = tensorflowTrainingService;

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
                            log.info("training is complete"); //we only train off-line not in realtime.  TODO reviewing this
                        }
                    }
                });

    }

    @Override
    public FantasyEventTypes firstTrainingEvent() {
        return first;
    }

}
