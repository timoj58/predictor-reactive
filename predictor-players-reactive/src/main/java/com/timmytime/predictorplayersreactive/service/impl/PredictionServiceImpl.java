package com.timmytime.predictorplayersreactive.service.impl;

import com.timmytime.predictorplayersreactive.enumerator.ApplicableFantasyLeagues;
import com.timmytime.predictorplayersreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayersreactive.model.FantasyOutcome;
import com.timmytime.predictorplayersreactive.model.Prediction;
import com.timmytime.predictorplayersreactive.request.PlayerEventOutcomeCsv;
import com.timmytime.predictorplayersreactive.request.TensorflowPrediction;
import com.timmytime.predictorplayersreactive.service.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service("predictionService")
public class PredictionServiceImpl implements PredictionService {

    private final Logger log = LoggerFactory.getLogger(PredictionServiceImpl.class);

    private final EventsService eventsService;
    private final PlayerService playerService;
    private final PlayerResponseService playerResponseService;
    private final TensorflowPredictionService tensorflowPredictionService;
    private final FantasyOutcomeService fantasyOutcomeService;
    private final Set<UUID> receipts = new HashSet<>();

    @Autowired
    public PredictionServiceImpl(
            EventsService eventsService,
            PlayerService playerService,
            PlayerResponseService playerResponseService,
            TensorflowPredictionService tensorflowPredictionService,
            FantasyOutcomeService fantasyOutcomeService
    ) {
        this.eventsService = eventsService;
        this.playerService = playerService;
        this.playerResponseService = playerResponseService;
        this.tensorflowPredictionService = tensorflowPredictionService;
        this.fantasyOutcomeService = fantasyOutcomeService;

        this.tensorflowPredictionService.setReceiptConsumer(id -> receipts.add(id));

        //init machine
        Flux.fromStream(
                Arrays.asList("assists",  "conceded",  "goals",  "minutes",  "red",  "saves",  "yellow").stream()
        ).subscribe(type -> tensorflowPredictionService.init(type));

    }

    @Override
    public void start(String country) {

        log.info("starting {}", country);

        //need to init all the types.
        Flux.fromStream(
                ApplicableFantasyLeagues.findByCountry(country).stream()
        ).subscribe(competition ->
                eventsService.get(competition.name().toLowerCase())
                        .subscribe(event -> {
                            log.info("processing {} v {}", event.getHome(), event.getAway());
                            processPlayers(competition.name().toLowerCase(), event.getDate(), event.getHome(), event.getAway());
                        })
        );
    }

    @Override
    public void result(UUID id, JSONObject result) {
        receipts.remove(id);
        CompletableFuture.runAsync(() ->
        tensorflowPredictionService.hasElements().subscribe(
                hasElements -> {

                    if(!hasElements && !receipts.isEmpty()){
                        fix().subscribe();
                    }

                    fantasyOutcomeService.find(id)
                            .subscribe(fantasyOutcome -> {
                                fantasyOutcome.setCurrent(Boolean.TRUE);
                                fantasyOutcome.setPrediction(
                                        normalize(result).toString()
                                );

                                log.info("saving prediction {} id: {}", fantasyOutcome.getFantasyEventType(), fantasyOutcome.getId());
                                fantasyOutcomeService.save(fantasyOutcome).doOnNext(
                                        outcome -> playerResponseService.addResult(outcome))
                                .doFinally(then -> {
                                    if(!hasElements && receipts.isEmpty()){
                                        //send message to client services that we have finished.
                                    }
                                })
                                .subscribe();

                            });
                }
        ));
    }

    @Override
    public Mono<Void> fix() {
        log.info("fixing predictions");

         fantasyOutcomeService.toFix()
                .subscribe(fantasyOutcome ->
                        tensorflowPredictionService.predict(
                                TensorflowPrediction.builder()
                                        .fantasyEventTypes(fantasyOutcome.getFantasyEventType())
                                        .playerEventOutcomeCsv(
                                                new PlayerEventOutcomeCsv(
                                                        fantasyOutcome.getId(),
                                                        fantasyOutcome.getPlayerId(),
                                                        fantasyOutcome.getOpponent(),
                                                        fantasyOutcome.getHome()))
                                        .build())
                );

         log.info("returning");

         return Mono.empty();
    }

    @Override
    public Mono<Long> toFix() {
        return Mono.from(
                fantasyOutcomeService.toFix().count()
        );
    }


    private Boolean processPlayers(String competition, LocalDateTime date, UUID homeTeam, UUID awayTeam) {
        Flux.fromStream(
                Stream.concat(
                        playerService.get(competition, homeTeam).stream(),
                        playerService.get(competition, awayTeam).stream())
        )
                .subscribe(player ->
                        Flux.fromStream(
                                Arrays.asList(FantasyEventTypes.values())
                                        .stream()
                                        .filter(f -> f.getPredict() == Boolean.TRUE) //need to remove saves...TODO
                        )
                                .subscribe(fantasyEvent ->
                                fantasyOutcomeService.save(
                                        FantasyOutcome.builder()
                                                .id(UUID.randomUUID())
                                                .eventDate(date)
                                                .opponent(player.getLatestTeam().equals(homeTeam) ? awayTeam : homeTeam)
                                                .playerId(player.getId())
                                                .fantasyEventType(fantasyEvent)
                                                .home(player.getLatestTeam().equals(homeTeam) ? "home" : "away") //not sure why its like this
                                                .build()
                                ).subscribe(fantasyOutcome ->
                                        tensorflowPredictionService.predict(
                                                TensorflowPrediction.builder()
                                                        .fantasyEventTypes(fantasyEvent)
                                                        .playerEventOutcomeCsv(
                                                                new PlayerEventOutcomeCsv(
                                                                        fantasyOutcome.getId(),
                                                                        player.getId(),
                                                                        player.getLatestTeam().equals(homeTeam) ? awayTeam : homeTeam,
                                                                        fantasyOutcome.getHome()))
                                                        .build()

                                        )
                                )
                        )
                );

        return Boolean.TRUE;
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
