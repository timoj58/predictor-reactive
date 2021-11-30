package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayerseventsreactive.facade.WebClientFacade;
import com.timmytime.predictorplayerseventsreactive.model.*;
import com.timmytime.predictorplayerseventsreactive.repo.PlayerResponseRepo;
import com.timmytime.predictorplayerseventsreactive.service.FantasyOutcomeService;
import com.timmytime.predictorplayerseventsreactive.service.PlayerResponseService;
import com.timmytime.predictorplayerseventsreactive.service.PlayerService;
import com.timmytime.predictorplayerseventsreactive.util.FantasyResponseTransformer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
@Service("playerResponseService")
public class PlayerResponseServiceImpl implements PlayerResponseService {

    private final PlayerResponseRepo playerResponseRepo;
    private final FantasyOutcomeService fantasyOutcomeService;
    private final PlayerService playerService;
    private final Map<UUID, List<FantasyEventTypes>> byPlayer = new HashMap<>();
    private final FantasyResponseTransformer fantasyResponseTransformer = new FantasyResponseTransformer();
    private final List<FantasyEventTypes> ALL_EVENTS =
            Arrays.stream(FantasyEventTypes.values()).filter(f -> f.getPredict() == Boolean.TRUE).collect(Collectors.toList());
    private Consumer<FantasyOutcome> consumer;

    @Autowired
    public PlayerResponseServiceImpl(
            PlayerService playerService,
            FantasyOutcomeService fantasyOutcomeService,
            PlayerResponseRepo playerResponseRepo
    ) {
        this.playerService = playerService;
        this.fantasyOutcomeService = fantasyOutcomeService;
        this.playerResponseRepo = playerResponseRepo;

        Flux<FantasyOutcome> receiver = Flux.create(sink -> consumer = sink::next, FluxSink.OverflowStrategy.BUFFER);
        receiver.subscribe(this::process);

    }

    @Override
    public void addResult(FantasyOutcome fantasyOutcome) {
        consumer.accept(fantasyOutcome);
    }

    @Override
    public Mono<PlayerResponse> getPlayer(UUID id) {
        return playerResponseRepo.findById(id);
    }

    private void load(UUID id) {

        playerService.get(id).subscribe(
                player -> {
                    PlayerResponse playerResponse = new PlayerResponse();
                    playerResponse.setId(player.getId());
                    playerResponse.setLabel(player.getLabel());
                    playerResponse.setCurrentTeam(player.getLatestTeam());

                    fantasyOutcomeService.findByPlayer(player.getId())
                            .filter(FantasyOutcome::getCurrent)
                            .doOnNext(p -> playerResponse.getFantasyOutcomes().add(p))
                            .doFinally(save -> {
                                //also need to work out, set
                                if (!playerResponse.getFantasyOutcomes().isEmpty()) {

                                    playerResponse.getFantasyOutcomes().stream().collect(groupingBy(FantasyOutcome::getOpponent))
                                            .values()
                                            .forEach(match -> {
                                                FantasyResponse fantasyResponse = fantasyResponseTransformer.transform.apply(match);

                                                UUID opponent = match.stream().map(FantasyOutcome::getOpponent).distinct().findFirst().get();
                                                Boolean isHome = match.stream().map(FantasyOutcome::getHome).distinct().findFirst().get().contentEquals("home");

                                                fantasyResponse.setOpponent(opponent);
                                                fantasyResponse.setIsHome(isHome);

                                                playerResponse.getFantasyResponse().add(fantasyResponse);
                                            });

                                }
                                playerResponseRepo.save(playerResponse).subscribe();
                            })
                            .subscribe();

                }
        );

    }


    private void process(FantasyOutcome fantasyOutcome) {
        if (!byPlayer.containsKey(fantasyOutcome.getPlayerId())) {
            byPlayer.put(fantasyOutcome.getPlayerId(), new ArrayList<>());
        }
        byPlayer.get(fantasyOutcome.getPlayerId()).add(fantasyOutcome.getFantasyEventType());

        if (byPlayer.get(fantasyOutcome.getPlayerId()).containsAll(ALL_EVENTS)) {
            log.info("loading player {}, all predictions available", fantasyOutcome.getPlayerId());
            load(fantasyOutcome.getPlayerId());
        }
    }

}
