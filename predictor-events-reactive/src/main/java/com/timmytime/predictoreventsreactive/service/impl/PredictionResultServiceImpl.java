package com.timmytime.predictoreventsreactive.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictoreventsreactive.cache.ReceiptCache;
import com.timmytime.predictoreventsreactive.enumerator.CountryCompetitions;
import com.timmytime.predictoreventsreactive.facade.WebClientFacade;
import com.timmytime.predictoreventsreactive.model.PredictionLine;
import com.timmytime.predictoreventsreactive.service.EventOutcomeService;
import com.timmytime.predictoreventsreactive.service.PredictionResultService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service("predictionResultService")
public class PredictionResultServiceImpl implements PredictionResultService {

    private static final Logger log = LoggerFactory.getLogger(PredictionResultServiceImpl.class);

    private final EventOutcomeService eventOutcomeService;
    private final WebClientFacade webClientFacade;
    private final ReceiptCache receiptCache;

    private final String clientHost;
    private final Integer competitionDelay;

    private final List<String> countriesProcessed = new ArrayList<>();

    @Autowired
    public PredictionResultServiceImpl(
            @Value("${client.host}") String clientHost,
            @Value("${competition.delay}") Integer competitionDelay,
            EventOutcomeService eventOutcomeService,
            WebClientFacade webClientFacade,
            ReceiptCache receiptCache
    ){
        this.clientHost = clientHost;
        this.competitionDelay = competitionDelay;

        this.eventOutcomeService = eventOutcomeService;
        this.webClientFacade = webClientFacade;
        this.receiptCache = receiptCache;
    }

    @Override
    public void addCountry(String country) {
        this.countriesProcessed.add(country);
    }

    @Override
    public void result(UUID id, JSONObject result,  Consumer<UUID> fix) {
        log.info("received a result {}", id.toString());
        CompletableFuture.runAsync(() ->
                //need to sort out the fix stuff.  to do.  on end of stream i guess. makes more sense...
                eventOutcomeService.find(id)
                        .subscribe(eventOutcome -> {
                            eventOutcome.setPrediction(normalize(result).toString());
                            log.info("saving {}", eventOutcome.getId());
                            eventOutcomeService.save(eventOutcome).subscribe();

                            Mono.just(receiptCache.isEmpty(id))
                                    .delayElement(Duration.ofMinutes(competitionDelay))
                                    .subscribe(empty -> { //check again.
                                        if(empty && receiptCache.isEmpty(id)){
                                            fix.accept(id);
                                        }else if(empty && receiptCache.isEmpty(id)){
                                            if(countriesProcessed.containsAll(Arrays.asList(CountryCompetitions.values()))){
                                                webClientFacade.sendMessage(clientHost+"/message", createMessage());
                                            }
                                        }
                                    });
                        })
        );
    }


    private List<PredictionLine> normalize(JSONObject result){
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

        byIndex.keySet().stream().forEach(
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

    private JsonNode createMessage(){
        try {
            return new ObjectMapper().readTree(
                    new JSONObject()
                            .put("type", "MATCH_PREDICTIONS").toString()
            );
        } catch (JsonProcessingException e) {
            log.error("message failed", e);

            return null;
        }
    }

}
