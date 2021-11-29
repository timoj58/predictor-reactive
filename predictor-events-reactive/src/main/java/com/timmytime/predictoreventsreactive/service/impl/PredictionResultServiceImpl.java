package com.timmytime.predictoreventsreactive.service.impl;

import com.timmytime.predictoreventsreactive.model.PredictionLine;
import com.timmytime.predictoreventsreactive.service.EventOutcomeService;
import com.timmytime.predictoreventsreactive.service.PredictionResultService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service("predictionResultService")
public class PredictionResultServiceImpl implements PredictionResultService {

    private final EventOutcomeService eventOutcomeService;

    @Autowired
    public PredictionResultServiceImpl(
            EventOutcomeService eventOutcomeService
    ) {
        this.eventOutcomeService = eventOutcomeService;
    }

    @Override
    public void result(UUID id, JSONObject result) {
        log.info("received a result {} {}", id.toString(), result.toString());
        CompletableFuture.runAsync(() ->
                eventOutcomeService.find(id)
                        .subscribe(eventOutcome -> {
                            eventOutcome.setPrediction(normalize(result).toString());
                            log.info("saving {}", eventOutcome.getId());
                            eventOutcomeService.save(eventOutcome).subscribe();
                        })
        );
    }


    private List<PredictionLine> normalize(JSONObject result) {
        //get our keys.
        Map<String, List<Double>> byIndex = new HashMap<>();


        Iterator<String> keys = result.keys();
        while (keys.hasNext()) {
            String key = keys.next();

            String keyLabel = result.getJSONObject(key).get("label").toString();
            if (!byIndex.containsKey(keyLabel)) {
                byIndex.put(keyLabel, new ArrayList<>());
            }

            byIndex.get(keyLabel).add(Double.valueOf(result.getJSONObject(key).get("score").toString()));
        }

        List<PredictionLine> normalized = new ArrayList<>();

        byIndex.keySet().forEach(
                key -> normalized.add(
                        new PredictionLine(key,
                                byIndex.get(key)
                                        .stream()
                                        .mapToDouble(m -> m).average().getAsDouble()))
        );

        return normalized
                .stream()
                .sorted((o1, o2) -> o2.getScore().compareTo(o1.getScore()))
                .collect(Collectors.toList());
    }


}
