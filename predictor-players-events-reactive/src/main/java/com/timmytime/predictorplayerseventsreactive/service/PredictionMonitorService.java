package com.timmytime.predictorplayerseventsreactive.service;

import com.timmytime.predictorplayerseventsreactive.enumerator.ApplicableFantasyLeagues;
import com.timmytime.predictorplayerseventsreactive.facade.WebClientFacade;
import com.timmytime.predictorplayerseventsreactive.request.Message;
import com.timmytime.predictorplayerseventsreactive.request.TensorflowPrediction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service("predictionMonitorService")
public class PredictionMonitorService {

    private final String messageHost;

    private final PredictionService predictionService;
    private final WebClientFacade webClientFacade;

    private final AtomicLong previousCount = new AtomicLong(0);
    private final AtomicBoolean process = new AtomicBoolean(true);
    private final List<String> countriesProcessed = new ArrayList<>();

    //private final Deque<TensorflowPrediction> queue = new ArrayDeque<>();


    @Autowired
    public PredictionMonitorService(
            @Value("${clients.message}") String messageHost,
            PredictionService predictionService,
            WebClientFacade webClientFacade
    ) {
        this.messageHost = messageHost;
        this.predictionService = predictionService;
        this.webClientFacade = webClientFacade;
    }

    public void addCountry(String country) {
        this.countriesProcessed.add(country);
    }



    @Scheduled(fixedDelay = 240000L) //once per 4 minutes is fine.
    public void predictionMonitor() {

        if (process.get()) {

            predictionService.outstanding()
                    .subscribe(count -> {
                        log.info("we have {} waiting ({})", count, previousCount.get());
                        if (count != 0 && count == previousCount.get()) {
                            log.info("reprocessing");
                            //needs seperate thread
                            CompletableFuture.runAsync(predictionService::reProcess);
                        } else if (count == 0 && countriesProcessed.containsAll(ApplicableFantasyLeagues.getCountries())) {
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
