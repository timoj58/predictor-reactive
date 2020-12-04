package com.timmytime.predictorplayersreactive.service.impl;

import com.timmytime.predictorplayersreactive.facade.WebClientFacade;
import com.timmytime.predictorplayersreactive.model.Prediction;
import com.timmytime.predictorplayersreactive.service.FantasyOutcomeService;
import com.timmytime.predictorplayersreactive.service.PlayerResponseService;
import com.timmytime.predictorplayersreactive.service.PredictionResultService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service("predictionResultService")
public class PredictionResultServiceImpl implements PredictionResultService {

    private final Logger log = LoggerFactory.getLogger(PredictionResultServiceImpl.class);


    private final FantasyOutcomeService fantasyOutcomeService;
    private final PlayerResponseService playerResponseService;
    private final WebClientFacade webClientFacade;

    @Autowired
    public PredictionResultServiceImpl(
            FantasyOutcomeService fantasyOutcomeService,
            PlayerResponseService playerResponseService,
            WebClientFacade webClientFacade
    ) {
        this.fantasyOutcomeService = fantasyOutcomeService;
        this.playerResponseService = playerResponseService;
        this.webClientFacade = webClientFacade;
    }

    @Override
    public void result(UUID id, JSONObject result, Consumer<UUID> fix) {
        CompletableFuture.runAsync(() ->

                fantasyOutcomeService.find(id)
                        .subscribe(fantasyOutcome -> {
                            fantasyOutcome.setCurrent(Boolean.TRUE);
                            fantasyOutcome.setPrediction(
                                    normalize(result).toString()
                            );

                            log.info("saving prediction {} id: {}", fantasyOutcome.getFantasyEventType(), fantasyOutcome.getId());
                            fantasyOutcomeService.save(fantasyOutcome).subscribe(
                                    outcome -> playerResponseService.addResult(outcome)
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

        byIndex.keySet().stream().forEach(
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
