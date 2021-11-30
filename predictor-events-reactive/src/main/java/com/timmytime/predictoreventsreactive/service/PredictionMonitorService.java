package com.timmytime.predictoreventsreactive.service;

import com.timmytime.predictoreventsreactive.enumerator.Predictions;
import com.timmytime.predictoreventsreactive.facade.WebClientFacade;
import com.timmytime.predictoreventsreactive.request.Message;
import com.timmytime.predictoreventsreactive.request.Prediction;
import com.timmytime.predictoreventsreactive.request.TensorflowPrediction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;


@Slf4j
@Service("predictionFinaliseService")
public class PredictionMonitorService {

    private final String messageHost;

    private final TensorflowPredictionService tensorflowPredictionService;
    private final EventOutcomeService eventOutcomeService;
    private final WebClientFacade webClientFacade;

    private final AtomicLong previousCount = new AtomicLong(0);
    private final AtomicBoolean process = new AtomicBoolean(true);
    private final AtomicBoolean start = new AtomicBoolean(false);

    private final Deque<TensorflowPrediction> predictionQueue = new ArrayDeque();

    @Autowired
    public PredictionMonitorService(
            @Value("${clients.message}") String messageHost,
            TensorflowPredictionService tensorflowPredictionService,
            EventOutcomeService eventOutcomeService,
            WebClientFacade webClientFacade
    ) {
        this.messageHost = messageHost;
        this.tensorflowPredictionService = tensorflowPredictionService;
        this.eventOutcomeService = eventOutcomeService;
        this.webClientFacade = webClientFacade;
    }

    public void addPrediction(TensorflowPrediction tensorflowPrediction){
         predictionQueue.add(tensorflowPrediction);
         if (!start.get())
             Mono.just(1).doOnNext(d -> next()).doFinally(set -> start.set(true))
                     .subscribe();
    }

    public void next(){
        log.info("queue size {}", predictionQueue.size());
        if (!predictionQueue.isEmpty())
          tensorflowPredictionService.predict(
                predictionQueue.pop()
        );
    }

    @Scheduled(fixedDelay = 120000L)
    public void predictionMonitor() {

        if (process.get() && predictionQueue.isEmpty() && start.get()) {

            eventOutcomeService.toFix().count()
                    .subscribe(count -> {
                        log.info("we have {} waiting ({})", count, previousCount.get());
                        if (count != 0 && count == previousCount.get()) {
                            log.info("reprocessing");
                            eventOutcomeService.toFix()
                                    .doOnNext(eventOutcome ->
                                            predictionQueue.add(
                                                    TensorflowPrediction.builder()
                                                    .predictions(Predictions.valueOf(eventOutcome.getEventType()))
                                                    .country(eventOutcome.getCountry())
                                                    .prediction(new Prediction(eventOutcome))
                                                    .build()))
                                    .doFinally(retry -> next())
                                    .subscribe();
                        } else if (count == 0) {
                            log.info("finishing");
                            webClientFacade.sendMessage(messageHost + "/message",
                                    Message.builder()
                                            .event("TEAMS_PREDICTED")
                                            .eventType("ALL")
                                            .build());
                            process.set(false);
                        }
                        previousCount.set(count);
                    });
        }

    }

}
