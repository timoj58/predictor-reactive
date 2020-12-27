package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.enumerator.ApplicableFantasyLeagues;
import com.timmytime.predictorplayerseventsreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayerseventsreactive.model.FantasyOutcome;
import com.timmytime.predictorplayerseventsreactive.request.PlayerEventOutcomeCsv;
import com.timmytime.predictorplayerseventsreactive.request.TensorflowPrediction;
import com.timmytime.predictorplayerseventsreactive.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Slf4j
@Service("predictionService")
public class PredictionServiceImpl implements PredictionService {

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

        //init machine
        Flux.fromStream(
                Arrays.asList("assists", "conceded", "goals", "minutes", "red", "saves", "yellow").stream()
        ).subscribe(type -> tensorflowPredictionService.init(type));

    }

    @Override
    public void start(String country) {

        log.info("starting {}", country);
        //need to init all the types.
        Flux.fromStream(
                ApplicableFantasyLeagues.findByCountry(country).stream()
        )
                .delayElements(Duration.ofMinutes(1))
                .subscribe(competition ->
                        eventsService.get(competition.name().toLowerCase())
                                .subscribe(event -> {
                                    log.info("processing {} v {}", event.getHome(), event.getAway());
                                    processPlayers(competition.name().toLowerCase(), event.getDate(), event.getHome(), event.getAway());
                                })
                );
    }

    @Override
    public Mono<Void> fix() {
        log.info("fixing predictions");
        reProcess();
        log.info("returning");

        return Mono.empty();
    }

    @Override
    public void reProcess() {

        log.info("processing to fix");
        //if we have no records left, then we should send a message to client service.  TODO. for automation.
        CompletableFuture.runAsync(() -> fantasyOutcomeService.toFix()
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
                )
        );
    }

    @Override
    public Mono<Long> outstanding() {
        return fantasyOutcomeService.toFix().count();
    }


    private Boolean processPlayers(String competition, LocalDateTime date, UUID homeTeam, UUID awayTeam) {
        Flux.fromStream(
                Stream.concat(
                        playerService.get(competition, homeTeam).stream(),
                        playerService.get(competition, awayTeam).stream())
        )
                .subscribe(player ->
                        Flux.fromArray(FantasyEventTypes.values())
                                .filter(f -> f.getPredict() == Boolean.TRUE)
                                //HARD.  TODO / IGNORE.  would save 30 minutes tho  .filter(f -> (f == FantasyEventTypes.SAVES && player.getIsGoalkeeper()) || f != FantasyEventTypes.SAVES)
                                .limitRate(1)
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

}