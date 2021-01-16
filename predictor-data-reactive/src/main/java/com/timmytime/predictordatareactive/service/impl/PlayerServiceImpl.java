package com.timmytime.predictordatareactive.service.impl;

import com.timmytime.predictordatareactive.enumerator.PlayerStats;
import com.timmytime.predictordatareactive.model.Player;
import com.timmytime.predictordatareactive.model.Team;
import com.timmytime.predictordatareactive.repo.PlayerRepo;
import com.timmytime.predictordatareactive.repo.StatMetricRepo;
import com.timmytime.predictordatareactive.service.PlayerService;
import com.timmytime.predictordatareactive.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Slf4j
@Service("playerService")
public class PlayerServiceImpl implements PlayerService {

    private final TeamService teamService;
    private final PlayerRepo playerRepo;
    private final StatMetricRepo statMetricRepo;


    @Override
    public Mono<Player> find(UUID id) {
        return null;
    }

    @Override
    public void delete(UUID id) {
        playerRepo.deleteById(id).subscribe();
    }

    @Override
    public Mono<Player> save(Player player) {
        return playerRepo.save(player);
    }

    @Override
    public List<Mono<Player>> process(JSONArray players) {
        List<JSONObject> playerNames = new ArrayList<>();
        IntStream.range(0, players.length()).forEach(i -> playerNames.add(players.getJSONObject(i)));

        return createLineup(playerNames);
    }

    @Override
    public Flux<Player> findByCompetition(String competition, String date, Boolean fantasy) {

        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        List<UUID> teams = teamService.getTeamsByCompetition(competition)
                .stream()
                .map(Team::getId)
                .collect(Collectors.toList());

        return playerRepo.findByLatestTeamIn(teams)
                .filter(f -> fantasy == f.getFantasyFootballer())
                .filter(f -> f.getLastAppearance().isAfter(localDate));
    }

    @Override
    public Flux<Player> findFantasyFootballers() {
        return playerRepo.findByFantasyFootballerTrue();
    }

    @Override
    public Mono<Void> createFantasyFootballers() {

        log.info("start create fantasy players");

        CompletableFuture.runAsync(() ->

                playerRepo.findAll()
                        .doOnNext(player -> playerRepo.save(
                                player.toBuilder()
                                        .fantasyFootballer(Boolean.FALSE)
                                        .build()
                        ).subscribe())
                        .doFinally(create -> {
                            log.info("now updating the players");
                            playerRepo.findAll()
                                    .filter(p -> p.getLastAppearance() != null && p.getLastAppearance().isAfter(LocalDate.now().minusYears(1)))
                                    .subscribe(player ->
                                            statMetricRepo.findByPlayer(player.getId())
                                                    .collectList()
                                                    .map(List::size)
                                                    .filter(f -> f > 0)
                                                    .subscribe(stats -> {
                                                        log.info("adding {}", player.getLabel());
                                                        playerRepo.save(
                                                                player
                                                                        .toBuilder()
                                                                        .fantasyFootballer(Boolean.TRUE)
                                                                        .build()
                                                        ).subscribe();
                                                    })

                                    );
                        })
                        .subscribe()
        );

        return Mono.empty();
    }

    @Override
    public Mono<Void> createGoalkeepers() {
        log.info("starting gk check");

        CompletableFuture.runAsync(() ->
                playerRepo.findByFantasyFootballerTrue()
                        .subscribe(player ->
                                statMetricRepo.findByPlayer(player.getId())
                                        .filter(f -> f.getLabel().equalsIgnoreCase("saves"))
                                        .collectList()
                                        .map(m -> m.size())
                                        .filter(f -> f > 0)
                                        .subscribe(gk -> {
                                            log.info("adding gk {}", player.getLabel());
                                            playerRepo.save(
                                                    player.toBuilder()
                                                            .isGoalkeeper(Boolean.TRUE)
                                                            .build()
                                            ).subscribe();
                                        })
                        )
        );

        return Mono.empty();
    }


    private List<Mono<Player>> createLineup(List<JSONObject> lineup) {

        List<Mono<Player>> players = new ArrayList<>();

        lineup.forEach(
                lineupPlayer ->
                        players.add(
                                playerRepo.findByLabel(lineupPlayer.getString("name"))
                                        .switchIfEmpty(
                                                Mono.just(
                                                        create(lineupPlayer.getString("name"))
                                                )
                                        )
                                        .map(m -> {
                                            m.getStats().addAll(getStats(lineupPlayer));
                                            return m;
                                        })
                                        .map(m -> {
                                            m.setDuration(getDuration(lineupPlayer));
                                            return m;
                                        })
                        )
        );

        return players;
    }

    private Integer getDuration(JSONObject player) {
        return player.getInt("time");
    }

    private List<JSONObject> getStats(JSONObject player) {
        List<JSONObject> stats = new ArrayList<>();
        Iterator<String> keys = player.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            if (PlayerStats.getKeys().contains(key)) {
                stats.add(new JSONObject().put(key, player.getString(key)));
            }
        }
        return stats;
    }

    private Player create(String label) {
        Player player = new Player();

        player.setId(UUID.randomUUID());
        player.setLabel(label);

        return player;
    }

}
