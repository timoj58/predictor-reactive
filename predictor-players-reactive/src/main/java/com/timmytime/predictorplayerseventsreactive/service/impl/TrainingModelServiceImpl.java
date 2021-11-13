package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.facade.WebClientFacade;
import com.timmytime.predictorplayerseventsreactive.model.Player;
import com.timmytime.predictorplayerseventsreactive.model.PlayersTrainingHistory;
import com.timmytime.predictorplayerseventsreactive.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

@Slf4j
@Service("trainingModelService")
public class TrainingModelServiceImpl implements TrainingModelService {

    private final WebClientFacade webClientFacade;
    private final PlayersTrainingHistoryService playersTrainingHistoryService;
    private final PlayerMatchService playerMatchService;
    private final TrainingService trainingService;
    private final TensorflowDataService tensorflowDataService;

    private final String dataHost;
    private final Boolean load;

    @Autowired
    public TrainingModelServiceImpl(
            @Value("${clients.data}") String dataHost,
            @Value("${training.load}") Boolean load,
            WebClientFacade webClientFacade,
            PlayersTrainingHistoryService playersTrainingHistoryService,
            PlayerMatchService playerMatchService,
            TrainingService trainingService,
            TensorflowDataService tensorflowDataService
    ) {
        this.dataHost = dataHost;
        this.load = load;
        this.webClientFacade = webClientFacade;
        this.playersTrainingHistoryService = playersTrainingHistoryService;
        this.playerMatchService = playerMatchService;
        this.trainingService = trainingService;
        this.tensorflowDataService = tensorflowDataService;
    }


    @Override
    public void create() {
        if (load) {
            getPlayers(f -> f.getLastAppearance().atStartOfDay().isAfter(LocalDateTime.now().minusYears(2)))
                    .limitRate(1)
                    .delayElements(Duration.ofSeconds(2))
                    .doOnNext(player -> playerMatchService.create(
                            player.getId(),
                            tensorflowDataService::load))
                    .doFinally(this::startTraining)
                    .subscribe();
        }else{
           this.startTraining(null);
        }
    }

    @Override
    public void next(PlayersTrainingHistory playersTrainingHistory) {
        CompletableFuture.runAsync(() ->
                getPlayers(f -> f.getLastAppearance().atStartOfDay().isAfter(playersTrainingHistory.getToDate()))
                        .limitRate(1)
                        .delayElements(Duration.ofMillis(500))
                        .doOnNext(player -> playerMatchService.next(
                                player.getId(),
                                playersTrainingHistory.getToDate().toLocalDate(),
                                tensorflowDataService::load)
                        )
                        .doFinally(this::startTraining)
                        .subscribe()
        );

    }

    private void startTraining(SignalType signalType) {
        playersTrainingHistoryService.find(trainingService.firstTrainingEvent()).subscribe(trainingService::train);
    }

    private Flux<Player> getPlayers(Predicate<Player> filter) {
        return webClientFacade.getPlayers(dataHost + "/players").filter(filter);
    }

}
