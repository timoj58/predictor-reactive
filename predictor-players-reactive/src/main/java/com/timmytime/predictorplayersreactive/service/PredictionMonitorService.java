package com.timmytime.predictorplayersreactive.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictorplayersreactive.enumerator.ApplicableFantasyLeagues;
import com.timmytime.predictorplayersreactive.facade.WebClientFacade;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service("predictionMonitorService")
public class PredictionMonitorService {

    private final String clientHost;

    private final PredictionService predictionService;
    private final WebClientFacade webClientFacade;

    private final AtomicLong previousCount = new AtomicLong(0);
    private final AtomicBoolean process = new AtomicBoolean(true);
    private final List<String> countriesProcessed = new ArrayList<>();


    @Autowired
    public PredictionMonitorService(
            @Value("${client.host}") String clientHost,
            PredictionService predictionService,
            WebClientFacade webClientFacade
    ) {
        this.clientHost = clientHost;
        this.predictionService = predictionService;
        this.webClientFacade = webClientFacade;
    }

    public void addCountry(String country) {
        this.countriesProcessed.add(country);
    }

    @Scheduled(fixedDelay = 60000L) //once per minute is fine.
    public void predictionMonitor() {

        if (process.get()) {

            predictionService.outstanding()
                    .subscribe(count -> {
                        log.info("we have {} waiting", count);
                        if (count != 0 && count == previousCount.get()) {
                            log.info("reprocessing");
                            predictionService.reProcess();
                        } else if (count == 0 && countriesProcessed.containsAll(ApplicableFantasyLeagues.getCountries())) {
                            log.info("finishing");
                            webClientFacade.sendMessage(clientHost + "/message", createMessage());
                            process.set(false);
                        }
                        previousCount.set(count);
                    });
        }

    }

    private JsonNode createMessage() {
        try {
            return new ObjectMapper().readTree(
                    new JSONObject()
                            .put("type", "PLAYER_PREDICTIONS").toString()
            );
        } catch (JsonProcessingException e) {
            log.error("message failed", e);

            return null;
        }
    }

}
