package com.timmytime.predictorplayersreactive.service.impl;

import com.timmytime.predictorplayersreactive.model.PlayersTrainingHistory;
import com.timmytime.predictorplayersreactive.service.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service("trainingService")
public class TrainingServiceImpl implements TrainingService {

    private final Logger log = LoggerFactory.getLogger(TrainingServiceImpl.class);

    private final PlayersTrainingHistoryService playersTrainingHistoryService;
    private final TensorflowDataService tensorflowDataService;
    private final TensorflowTrainingService tensorflowTrainingService;
    private final PlayerMatchService playerMatchService;

    private final Integer interval;

    @Autowired
    public TrainingServiceImpl(
            @Value("${training.interval}") Integer interval,
            PlayersTrainingHistoryService playersTrainingHistoryService,
            TensorflowDataService tensorflowDataService,
            TensorflowTrainingService tensorflowTrainingService,
            PlayerMatchService playerMatchService
    ){
        this.interval = interval;
        this.playersTrainingHistoryService = playersTrainingHistoryService;
        this.tensorflowDataService = tensorflowDataService;
        this.tensorflowTrainingService = tensorflowTrainingService;
        this.playerMatchService = playerMatchService;
    }


    @Override
    public void train() {

        playersTrainingHistoryService.find()
                .subscribe(history ->
                        playerMatchService.get(
                                history.getToDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                                history.getToDate().plusYears(interval).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                        )
                .doOnNext(playerMatch -> tensorflowDataService.load(playerMatch))
                .doFinally(train ->
                    playersTrainingHistoryService.save(
                           new PlayersTrainingHistory(history.getToDate(), history.getToDate().plusYears(interval))
                    ).subscribe(trainingHistory -> tensorflowTrainingService.train(trainingHistory.getId()))
                ));

    }



    @PostConstruct
    private void init(){

        playersTrainingHistoryService.find()
                .switchIfEmpty(Mono.just(new PlayersTrainingHistory()))
                .subscribe(history -> {

                    if(history.getId() == null){
                        log.info("init record");
                        history = new PlayersTrainingHistory(
                                LocalDate.parse("01-08-2009", DateTimeFormatter.ofPattern("dd-MM-yyyy")).atStartOfDay(),
                                LocalDate.parse("01-08-2009", DateTimeFormatter.ofPattern("dd-MM-yyyy")).atStartOfDay()
                                );

                        history.setCompleted(Boolean.TRUE);

                        playersTrainingHistoryService.save(history).subscribe();
                    }

                });

    }

}
