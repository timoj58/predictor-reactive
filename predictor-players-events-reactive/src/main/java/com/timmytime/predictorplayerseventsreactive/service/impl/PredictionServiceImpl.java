package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.enumerator.ApplicableFantasyLeagues;
import com.timmytime.predictorplayerseventsreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayerseventsreactive.model.FantasyOutcome;
import com.timmytime.predictorplayerseventsreactive.request.PlayerEventOutcomeCsv;
import com.timmytime.predictorplayerseventsreactive.request.TensorflowPrediction;
import com.timmytime.predictorplayerseventsreactive.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Service("predictionService")
public class PredictionServiceImpl implements PredictionService {

    private final EventsService eventsService;
    private final PlayerService playerService;
    private final FantasyOutcomeService fantasyOutcomeService;
    private final PredictionMonitorService predictionMonitorService;

    private Consumer<Pair<FantasyEventTypes, List<FantasyOutcome>>> consumer;

    @Autowired
    public PredictionServiceImpl(
            EventsService eventsService,
            PlayerService playerService,
            FantasyOutcomeService fantasyOutcomeService,
            PredictionMonitorService predictionMonitorService
    ) {
        this.eventsService = eventsService;
        this.playerService = playerService;
        this.fantasyOutcomeService = fantasyOutcomeService;
        this.predictionMonitorService = predictionMonitorService;

        Flux<Pair<FantasyEventTypes, List<FantasyOutcome>>> receiver = Flux.create(sink -> consumer = sink::next, FluxSink.OverflowStrategy.BUFFER);
        receiver.limitRate(10).subscribe(this::process);
    }

    @Override
    public void start(String country) {
        log.info("starting {}", country);
        //need to init all the types.
        Flux.fromStream(
                        ApplicableFantasyLeagues.findByCountry(country).stream()
                )
                .subscribe(competition -> {
                            predictionMonitorService.setStart();
                            eventsService.get(competition.name().toLowerCase())
                                    .subscribe(event -> {
                                        log.info("processing {} v {}", event.getHome(), event.getAway());
                                        processPlayers(competition.name().toLowerCase(), event.getDate(), event.getHome(), event.getAway());
                                    });
                        }
                );
    }


    private void processPlayers(String competition, LocalDateTime date, UUID homeTeam, UUID awayTeam) {
        Flux.concat(
                        playerService.get(competition, homeTeam), playerService.get(competition, awayTeam)
                )
                .collectList()
                .subscribe(players ->
                        Flux.fromArray(FantasyEventTypes.values())
                                .filter(f -> f.getPredict() == Boolean.TRUE)
                                .limitRate(1)
                                .subscribe(fantasyEvent -> {

                                            List<FantasyOutcome> playersOutcomes = new ArrayList<>();

                                            players.forEach(
                                                    player ->
                                                            playersOutcomes.add(FantasyOutcome.builder()
                                                                    .id(UUID.randomUUID())
                                                                    .eventDate(date)
                                                                    .opponent(player.getLatestTeam().equals(homeTeam) ? awayTeam : homeTeam)
                                                                    .playerId(player.getId())
                                                                    .fantasyEventType(fantasyEvent)
                                                                    .home(player.getLatestTeam().equals(homeTeam) ? "home" : "away") //not sure why its like this
                                                                    .build()
                                                            ));

                                            consumer.accept(Pair.of(fantasyEvent, playersOutcomes));

                                        }

                                )
                );

    }

    private void process(Pair<FantasyEventTypes, List<FantasyOutcome>> fantasyOutcomes) {

        fantasyOutcomes.getRight()
                .forEach(
                        fantasyOutcome -> fantasyOutcomeService.save(fantasyOutcome).subscribe()
                );

        predictionMonitorService.addPrediction(
                TensorflowPrediction.builder()
                        .fantasyEventTypes(fantasyOutcomes.getLeft())
                        .playerEventOutcomeCsv(
                                fantasyOutcomes.getRight()
                                        .stream()
                                        .map(saved ->
                                                new PlayerEventOutcomeCsv(
                                                        saved.getId(),
                                                        saved.getPlayerId(),
                                                        saved.getOpponent(),
                                                        saved.getHome()))
                                        .collect(Collectors.toList()))

                        .build());

    }

}
