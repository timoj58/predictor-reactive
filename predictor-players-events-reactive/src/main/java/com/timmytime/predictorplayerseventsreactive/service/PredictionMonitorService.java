package com.timmytime.predictorplayerseventsreactive.service;

import com.timmytime.predictorplayerseventsreactive.facade.WebClientFacade;
import com.timmytime.predictorplayerseventsreactive.request.Message;
import com.timmytime.predictorplayerseventsreactive.request.PlayerEventOutcomeCsv;
import com.timmytime.predictorplayerseventsreactive.request.TensorflowPrediction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service("predictionMonitorService")
public class PredictionMonitorService {

    private final String messageHost;

    private final WebClientFacade webClientFacade;
    private final FantasyOutcomeService fantasyOutcomeService;
    private final TensorflowPredictionService tensorflowPredictionService;

    private final AtomicLong previousCount = new AtomicLong(0);
    private final AtomicBoolean process = new AtomicBoolean(true);
    private final AtomicBoolean start = new AtomicBoolean(false);
    private final BlockingDeque<TensorflowPrediction> predictionQueue = new LinkedBlockingDeque<>();


    @Autowired
    public PredictionMonitorService(
            @Value("${clients.message}") String messageHost,
            WebClientFacade webClientFacade,
            FantasyOutcomeService fantasyOutcomeService,
            TensorflowPredictionService tensorflowPredictionService
    ) {
        this.messageHost = messageHost;
        this.webClientFacade = webClientFacade;
        this.fantasyOutcomeService = fantasyOutcomeService;
        this.tensorflowPredictionService = tensorflowPredictionService;
    }

    public void addPrediction(TensorflowPrediction tensorflowPrediction){
        log.info("pushing to queue {}", predictionQueue.size());
        predictionQueue.push(tensorflowPrediction);
        if (!start.get() || predictionQueue.size() == 1)
            Mono.just(1).doOnNext(d -> next()).doFinally(set -> start.set(true))
                    .subscribe();
    }

    public void next(){
        log.info("popping from queue {}", predictionQueue.size());
        if (!predictionQueue.isEmpty())
            tensorflowPredictionService.predict(
                    predictionQueue.pop()
            );
    }


    @Scheduled(fixedDelay = 240000L) //once per 4 minutes is fine.
    public void predictionMonitor() {

        if (process.get() && predictionQueue.isEmpty() && start.get()) {

            fantasyOutcomeService.toFix().count()
                    .subscribe(count -> {
                        log.info("we have {} waiting ({})", count, previousCount.get());
                        if (count != 0 && count == previousCount.get()) {
                            log.info("reprocessing");
                            fantasyOutcomeService.toFix()
                                    .doOnNext(fantasyOutcome ->
                                            predictionQueue.push(
                                                    TensorflowPrediction.builder()
                                                            .fantasyEventTypes(fantasyOutcome.getFantasyEventType())
                                                            .playerEventOutcomeCsv(
                                                                    new PlayerEventOutcomeCsv(
                                                                            fantasyOutcome.getId(),
                                                                            fantasyOutcome.getPlayerId(),
                                                                            fantasyOutcome.getOpponent(),
                                                                            fantasyOutcome.getHome()))
                                                            .build())
                                    )
                                    .doFinally(retry -> next())
                                    .subscribe();

                        } else if (count == 0) {
                            log.info("finishing");
                            webClientFacade.sendMessage(messageHost + "/message",
                                    Message.builder()
                                            .event("PLAYERS_PREDICTED")
                                            .eventType("ALL")
                                            .build());
                            process.set(false);
                        }
                        previousCount.set(count);
                    });
        }

    }

}
