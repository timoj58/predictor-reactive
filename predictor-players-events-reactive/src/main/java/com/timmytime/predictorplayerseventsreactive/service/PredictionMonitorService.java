package com.timmytime.predictorplayerseventsreactive.service;

import com.timmytime.predictorplayerseventsreactive.enumerator.ApplicableFantasyLeagues;
import com.timmytime.predictorplayerseventsreactive.facade.WebClientFacade;
import com.timmytime.predictorplayerseventsreactive.request.Message;
import com.timmytime.predictorplayerseventsreactive.request.PlayerEventOutcomeCsv;
import com.timmytime.predictorplayerseventsreactive.request.TensorflowPrediction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

@Slf4j
@Service("predictionMonitorService")
public class PredictionMonitorService {

    private final String messageHost;

    private final WebClientFacade webClientFacade;
    private final FantasyOutcomeService fantasyOutcomeService;
    private final TensorflowPredictionService tensorflowPredictionService;

    private final AtomicLong previousCount = new AtomicLong(0);
    private final AtomicInteger started = new AtomicInteger(0);
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

        new Thread(() -> {
            try {
                tensorflowPredictionService.predict(
                        predictionQueue.take()
                );
            } catch (InterruptedException e) {
                log.error("start", e);
            }
        }).start();

    }

    public void setStart() {
        if (1 == started.incrementAndGet()) {
            //init machine
            Flux.fromStream(
                    Stream.of("assists", "goals", "yellow")
            ).subscribe(tensorflowPredictionService::init);
        }
    }

    public void addPrediction(TensorflowPrediction tensorflowPrediction) {
        log.info("pushing to queue {}", predictionQueue.size());
        predictionQueue.push(tensorflowPrediction);
    }

     public void next() {
        log.info("popping from queue {}", predictionQueue.size());
        if (!predictionQueue.isEmpty()) {
            tensorflowPredictionService.predict(
                    predictionQueue.pop()
            );
        }
    }


    @Scheduled(fixedDelay = 120000L) //shorter interval is better
    public void predictionMonitor() {

        if (predictionQueue.isEmpty() && started.get() == ApplicableFantasyLeagues.values().length) {

            fantasyOutcomeService.toFix().count()
                    .switchIfEmpty(Mono.just(0L))
                    .subscribe(count -> {
                        log.info("we have {} waiting ({})", count, previousCount.get());
                        if (count != 0 && count == previousCount.get()) {
                            log.info("reprocessing");
                            fantasyOutcomeService.toFix()
                                    //Need to confirm performance increases
                                    .doOnNext(fantasyOutcome ->
                                            predictionQueue.push(
                                                    TensorflowPrediction.builder()
                                                            .fantasyEventTypes(fantasyOutcome.getFantasyEventType())
                                                            .playerEventOutcomeCsv(
                                                                    List.of(
                                                                            new PlayerEventOutcomeCsv(
                                                                                    fantasyOutcome.getId(),
                                                                                    fantasyOutcome.getPlayerId(),
                                                                                    fantasyOutcome.getOpponent(),
                                                                                    fantasyOutcome.getHome())))
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
                            started.set(0);
                        }
                        previousCount.set(count);
                    });
        }

    }

}
