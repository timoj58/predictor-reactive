package com.timmytime.predictorplayersreactive.service.impl;

import com.timmytime.predictorplayersreactive.model.Player;
import com.timmytime.predictorplayersreactive.model.PlayersTrainingHistory;
import com.timmytime.predictorplayersreactive.service.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service("trainingService")
public class TrainingServiceImpl implements TrainingService {

    private final Logger log = LoggerFactory.getLogger(TrainingServiceImpl.class);

    private final PlayerService playerService;
    private final PlayersTrainingHistoryService playersTrainingHistoryService;
    private final TensorflowTrainingService tensorflowTrainingService;
    private final PlayerMatchService playerMatchService;

    private final Integer interval;

    @Autowired
    public TrainingServiceImpl(
            @Value("${training.interval}") Integer interval,
            PlayerService playerService,
            PlayersTrainingHistoryService playersTrainingHistoryService,
            TensorflowTrainingService tensorflowTrainingService,
            PlayerMatchService playerMatchService
    ){
        this.interval = interval;
        this.playerService = playerService;
        this.playersTrainingHistoryService = playersTrainingHistoryService;
        this.tensorflowTrainingService = tensorflowTrainingService;
        this.playerMatchService = playerMatchService;
    }


    @Override
    public void train() {


        UUID nextHistoryId = UUID.randomUUID();

        List<Player> players = playerService.get();
        Integer playerCount = players.size();

        log.info("training init {} for {} players", nextHistoryId, playerCount);

        //only running this once so if it takes time (ie 15 minutes per cycle its ok).
        //trying to avoid redis, and this is only for retraining once (rarely used).

        playersTrainingHistoryService.find()
                .doOnNext(history ->
                    playersTrainingHistoryService.save(
                            new PlayersTrainingHistory(
                                    nextHistoryId,
                                    history.getToDate(),
                                    history.getToDate().plusYears(interval)
                            )
                    ).subscribe(trainingHistory -> {
                        String fromDate = trainingHistory.getFromDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                        String toDate = trainingHistory.getToDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                        Flux.fromStream(
                                players.stream()
                        ).limitRate(100)
                                .delayElements(Duration.ofMillis(250))
                                .subscribe(player -> playerMatchService.create(
                                        player.getId(),
                                        fromDate,
                                        toDate));
                    })
                ).doFinally(train ->
                Mono.just(nextHistoryId)
                        .delayElement(Duration.ofMillis(250 * playerCount)) //to review in a bit.
                        .subscribe(id -> tensorflowTrainingService.train(id)))
        .subscribe();

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
