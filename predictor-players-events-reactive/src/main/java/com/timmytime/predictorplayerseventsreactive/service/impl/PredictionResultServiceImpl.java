package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.model.Prediction;
import com.timmytime.predictorplayerseventsreactive.service.FantasyOutcomeService;
import com.timmytime.predictorplayerseventsreactive.service.PlayerResponseService;
import com.timmytime.predictorplayerseventsreactive.service.PredictionResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service("predictionResultService")
public class PredictionResultServiceImpl implements PredictionResultService {

    private final FantasyOutcomeService fantasyOutcomeService;
    private final PlayerResponseService playerResponseService;

    @Override
    public void result(UUID id, JSONObject result, Consumer<UUID> fix) {
        log.info("processing prediction result");
        CompletableFuture.runAsync(() ->

                fantasyOutcomeService.find(id)
                        .subscribe(fantasyOutcome -> {
                            fantasyOutcome.setCurrent(Boolean.TRUE);
                            fantasyOutcome.setPrediction(
                                    normalize(result).toString()
                            );

                            log.info("saving prediction {} id: {}", fantasyOutcome.getFantasyEventType(), fantasyOutcome.getId());
                            fantasyOutcomeService.save(fantasyOutcome).subscribe(
                                    playerResponseService::addResult
                            );

                        })
        );
    }

    private List<Prediction> normalize(JSONObject result) {

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


        List<Prediction> normalized = new ArrayList<>();

        byIndex.keySet().forEach(
                key -> normalized.add(
                        new Prediction(key,
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
