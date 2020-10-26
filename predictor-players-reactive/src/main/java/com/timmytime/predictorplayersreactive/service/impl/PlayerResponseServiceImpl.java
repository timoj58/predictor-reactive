package com.timmytime.predictorplayersreactive.service.impl;

import com.timmytime.predictorplayersreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayersreactive.facade.WebClientFacade;
import com.timmytime.predictorplayersreactive.model.*;
import com.timmytime.predictorplayersreactive.repo.PlayerResponseRepo;
import com.timmytime.predictorplayersreactive.service.*;
import com.timmytime.predictorplayersreactive.util.FantasyResponseTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service("playerResponseService")
public class PlayerResponseServiceImpl implements PlayerResponseService {

    private final Logger log = LoggerFactory.getLogger(PlayerResponseServiceImpl.class);

    private final PlayerResponseRepo playerResponseRepo;
    private final FantasyOutcomeService fantasyOutcomeService;
    private final PlayerService playerService;
    private final TeamService teamService;
    private final PlayerMatchService playerMatchService;

    private final Flux<FantasyOutcome> receiver;
    private Consumer<FantasyOutcome> consumer;

    private final Integer delay;

    private final Map<UUID, List<FantasyEventTypes>> byPlayer = new HashMap<>();

    private final FantasyResponseTransformer fantasyResponseTransformer = new FantasyResponseTransformer();

    private final List<FantasyEventTypes> ALL_EVENTS =
            Arrays.asList(FantasyEventTypes.values()).stream().filter(f -> f.getPredict() == Boolean.TRUE).collect(Collectors.toList());

    BiFunction<List<PlayerMatch>, FantasyEventTypes, Integer> getTotals = (playerAppearances, fantasyEventTypes) -> {

        //TODO tidy this all up from old code a mess now
        List<PlayerEvent> statMetrics = new ArrayList<>();
        playerAppearances.stream().forEach(
                playerAppearance -> playerAppearance.getStats().stream().forEach(stat -> statMetrics.add(new PlayerEvent(stat))));

        return statMetrics.stream().filter(f -> f.getEventType().equals(fantasyEventTypes)).mapToInt(m -> m.getValue()).sum();
    };

    @Autowired
    public PlayerResponseServiceImpl(
            @Value("${delay}") Integer delay,
            PlayerService playerService,
            FantasyOutcomeService fantasyOutcomeService,
            TeamService teamService,
            PlayerMatchService playerMatchService,
            PlayerResponseRepo playerResponseRepo
    ){
        this.delay = delay;
        this.playerService = playerService;
        this.fantasyOutcomeService = fantasyOutcomeService;
        this.teamService = teamService;
        this.playerMatchService = playerMatchService;
        this.playerResponseRepo = playerResponseRepo;

        this.receiver
                = Flux.push(sink -> consumer = (t) -> sink.next(t), FluxSink.OverflowStrategy.BUFFER);

        this.receiver.subscribe(this::process);

    }

    @Override
    public void addResult(FantasyOutcome fantasyOutcome) {
        log.info("adding completed fantasy outcome");
        consumer.accept(fantasyOutcome);
    }

    @Override
    public Mono<PlayerResponse> getPlayer(UUID id) {
        return playerResponseRepo.findById(id);
    }

    private void load(UUID id, List<PlayerMatch> playerMatches) {

        Player player = playerService.get(id);

        PlayerResponse playerResponse = new PlayerResponse();
        playerResponse.setId(player.getId());
        playerResponse.setLabel(player.getLabel());
        playerResponse.setCurrentTeam(teamService.getTeam(player.getLatestTeam()).getLabel());

        log.info("we have {} matches for {}", playerMatches.size(), player.getLabel());

        List<FantasyOutcome> fantasyOutcomesValidated = new ArrayList<>();
        List<FantasyOutcome> fantasyOutcomes = new ArrayList<>();

        fantasyOutcomeService.findByPlayer(player.getId())
               .doOnNext(fantasyOutcome -> {
                   if(fantasyOutcome.getCurrent())
                   {
                       fantasyOutcomes.add(fantasyOutcome);
                   }
                   else {
                       fantasyOutcomesValidated.add(fantasyOutcome);
                   }
               })
               .doFinally(save -> {
                   if(!fantasyOutcomesValidated.isEmpty()) {
                       Arrays.asList(FantasyEventTypes.values())
                               .stream()
                               .filter(f -> f.getPredict() == Boolean.TRUE)
                               .forEach(event -> {
                                   List<FantasyOutcome> filtered = fantasyOutcomesValidated.stream().filter(f -> f.getFantasyEventType().equals(event)).collect(Collectors.toList());
                                   playerResponse.getAverages().add(new FantasyEvent(fantasyResponseTransformer.total.apply(filtered, event) / filtered.size(), event.name().toLowerCase()));
                               });
                   }

                   //also need to work out, set
                   playerResponse.setAppearances(playerMatches.size());
                   //need to sort these out at some point.  needs util to do it.
                   playerResponse.setGoals(getTotals.apply(playerMatches, FantasyEventTypes.GOALS));
                   playerResponse.setAssists(getTotals.apply(playerMatches, FantasyEventTypes.ASSISTS));
                   playerResponse.setRedCards(getTotals.apply(playerMatches, FantasyEventTypes.RED_CARD));
                   playerResponse.setYellowCards(getTotals.apply(playerMatches, FantasyEventTypes.YELLOW_CARD));
                   playerResponse.setSaves(getTotals.apply(playerMatches, FantasyEventTypes.SAVES));

                   //set our current status flags too (historic is worked out in app).
                   List<PlayerMatch> recent = playerMatches
                           .stream()
                           .filter(f -> f.getDate().toLocalDate().isAfter(LocalDate.now().minusYears(1)))
                           .collect(Collectors.toList());

                   if(!recent.isEmpty()) {
                       playerResponse.setHardmanYellow((getTotals.apply(recent, FantasyEventTypes.YELLOW_CARD).doubleValue() / (double)recent.size()) * 100.0);
                       playerResponse.setHardmanRed((getTotals.apply(recent, FantasyEventTypes.RED_CARD).doubleValue() / (double)recent.size()) * 100.0);
                       playerResponse.setWizard( (getTotals.apply(recent, FantasyEventTypes.ASSISTS).doubleValue() / (double)recent.size()) * 100.0);
                       playerResponse.setMarksman((getTotals.apply(recent, FantasyEventTypes.GOALS).doubleValue() / (double)recent.size()) * 100.0);
                   }

                   if(!fantasyOutcomes.isEmpty()) {

                       fantasyOutcomes.stream().collect(groupingBy(FantasyOutcome::getOpponent))
                               .values()
                               .stream()
                               .forEach(match -> {
                                   FantasyResponse fantasyResponse = fantasyResponseTransformer.transform.apply(match);

                                   UUID opponent = match.stream().map(m -> m.getOpponent()).distinct().findFirst().get();
                                   Boolean isHome = match.stream().map(m -> m.getHome()).distinct().findFirst().get().contentEquals("home");

                                   fantasyResponse.setOpponent(teamService.getTeam(opponent).getLabel());
                                   fantasyResponse.setIsHome(isHome);

                                   playerResponse.getFantasyResponse().add(fantasyResponse);
                               });


                   }

                   playerResponseRepo.save(playerResponse).subscribe();
               })
               .subscribe();

    }



    private void process(FantasyOutcome fantasyOutcome){
        if (!byPlayer.containsKey(fantasyOutcome.getPlayerId())) {
            byPlayer.put(fantasyOutcome.getPlayerId(), new ArrayList<>());
        }
        byPlayer.get(fantasyOutcome.getPlayerId()).add(fantasyOutcome.getFantasyEventType());

        List<PlayerMatch> playerMatches = new ArrayList<>();

        if(byPlayer.get(fantasyOutcome.getPlayerId()).containsAll(ALL_EVENTS)){
            log.info("loading player {}, all predictions available", fantasyOutcome.getPlayerId());
            Mono.just(fantasyOutcome.getPlayerId())
                    .doOnNext(player -> playerMatchService.create(
                            player,
                            "01-08-2009",
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                            (data) -> playerMatches.add(data)
                    ))
            .doFinally(start -> Mono.just(fantasyOutcome)
                    .delayElement(Duration.ofSeconds(10 * delay))
                    .subscribe(outcome -> load(outcome.getPlayerId(), playerMatches)))
            .subscribe();

        }
    }

    @PostConstruct
    private void init(){
        playerResponseRepo.deleteAll();
    }

}
