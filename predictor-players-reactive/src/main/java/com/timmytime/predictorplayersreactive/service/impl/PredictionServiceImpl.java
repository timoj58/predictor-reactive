package com.timmytime.predictorplayersreactive.service.impl;

import com.timmytime.predictorplayersreactive.enumerator.ApplicableFantasyLeagues;
import com.timmytime.predictorplayersreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayersreactive.model.FantasyOutcome;
import com.timmytime.predictorplayersreactive.model.Prediction;
import com.timmytime.predictorplayersreactive.request.PlayerEventOutcomeCsv;
import com.timmytime.predictorplayersreactive.service.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service("predictionService")
public class PredictionServiceImpl implements PredictionService {

    private final Logger log = LoggerFactory.getLogger(PredictionServiceImpl.class);

    private final EventsService eventsService;
    private final PlayerService playerService;
    private final TensorflowPredictionService tensorflowPredictionService;
    private final FantasyOutcomeService fantasyOutcomeService;

    @Autowired
    public PredictionServiceImpl(
            EventsService eventsService,
            PlayerService playerService,
            TensorflowPredictionService tensorflowPredictionService,
            FantasyOutcomeService fantasyOutcomeService
    ) {
        this.eventsService = eventsService;
        this.playerService = playerService;
        this.tensorflowPredictionService = tensorflowPredictionService;
        this.fantasyOutcomeService = fantasyOutcomeService;
    }

    @Override
    public void start(String country) {

        log.info("starting {}", country);

        Flux.fromStream(
                ApplicableFantasyLeagues.findByCountry(country).stream()
        ).subscribe(competition ->
                eventsService.get(competition.name().toLowerCase())
                        .delayElements(Duration.ofSeconds(1))
                        .subscribe(event -> {
                            log.info("processing {} v {}", event.getHome(), event.getAway());
                             processPlayers(competition.name().toLowerCase(), event.getDate(), event.getHome(), event.getAway(), Boolean.TRUE);
                             processPlayers(competition.name().toLowerCase(), event.getDate(), event.getAway(), event.getHome(), Boolean.FALSE);
                        })
        );

    }

    @Override
    public void result(UUID id, JSONObject result) {
        fantasyOutcomeService.find(id)
                .subscribe(fantasyOutcome -> {
                    fantasyOutcome.setPrediction(
                            normalize(result).toString()
                    );

                    fantasyOutcomeService.save(fantasyOutcome).subscribe();
                });

    }

    private void processPlayers(String competition, LocalDateTime date, UUID team, UUID opponent, Boolean home){
        Flux.fromStream(
                playerService.get(competition, team).stream()
        ).delayElements(Duration.ofMillis(100))
                .subscribe(player ->
                Flux.fromStream(
                        Arrays.asList(FantasyEventTypes.values())
                        .stream()
                        .filter(f -> f.getPredict() == Boolean.TRUE)
                ).delayElements(Duration.ofMillis(100))
                        .subscribe(fantasyEvent ->
                                fantasyOutcomeService.save(
                                        FantasyOutcome.builder()
                                                .eventDate(date)
                                                .opponent(opponent)
                                                .playerId(player.getId())
                                                .fantasyEventType(fantasyEvent)
                                                .home(home ? "home" : "away") //not sure why its like this
                                                .build()
                                ).subscribe(fantasyOutcome ->
                                        tensorflowPredictionService.predict(fantasyEvent,
                                                new PlayerEventOutcomeCsv(
                                                        fantasyOutcome.getId(),
                                                        player.getId(),
                                                        opponent,
                                                        fantasyOutcome.getHome())
                                        )
                                )
                        )
        );
    }

    private List<Prediction> normalize(JSONObject result){

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
