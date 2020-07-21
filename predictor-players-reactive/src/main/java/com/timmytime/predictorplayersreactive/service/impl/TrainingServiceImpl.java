package com.timmytime.predictorplayersreactive.service.impl;

import com.timmytime.predictorplayersreactive.model.PlayersTrainingHistory;
import com.timmytime.predictorplayersreactive.service.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service("trainingService")
public class TrainingServiceImpl implements TrainingService {

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

    /*
      need to change this to only train by interval, given dont want to load all the data
      anymore....TODO.  lots of changes including python
     */

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


    //TBC need to set this all back up.  1/8/2009 onwards....
    @PostConstruct
    private void init(){

    } //makes sense in reality, to actually re-train everything, so its tested...
     //as part of deployment.  yes.  parrallel builds for now.

}
