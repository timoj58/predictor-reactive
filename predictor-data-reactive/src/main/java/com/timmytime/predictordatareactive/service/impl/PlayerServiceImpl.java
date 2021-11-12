package com.timmytime.predictordatareactive.service.impl;

import com.timmytime.predictordatareactive.enumerator.CountryCompetitions;
import com.timmytime.predictordatareactive.enumerator.PlayerStats;
import com.timmytime.predictordatareactive.model.Player;
import com.timmytime.predictordatareactive.model.Team;
import com.timmytime.predictordatareactive.repo.PlayerRepo;
import com.timmytime.predictordatareactive.repo.StatMetricRepo;
import com.timmytime.predictordatareactive.service.PlayerService;
import com.timmytime.predictordatareactive.service.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service("playerService")
public class PlayerServiceImpl implements PlayerService {

    private final TeamService teamService;
    private final PlayerRepo playerRepo;

    @Autowired
    public PlayerServiceImpl(
            TeamService teamService,
            PlayerRepo playerRepo
    ) {
        this.teamService = teamService;
        this.playerRepo = playerRepo;
    }

    @Override
    public Mono<Player> find(UUID id) {
        return null;
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
    public Flux<Player> findByCompetition(String competition, String date) {

        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        List<UUID> teams = teamService.getTeamsByCompetition(competition)
                .stream()
                .map(Team::getId)
                .collect(Collectors.toList());

        return playerRepo.findByLatestTeamIn(teams)
                .filter(f -> f.getLastAppearance().isAfter(localDate));
    }

    @Override
    public Flux<Player> findAll() {
        return playerRepo.findAll();
    }


    private List<Mono<Player>> createLineup(List<JSONObject> lineup) {

        List<Mono<Player>> players = new ArrayList<>();

        lineup.forEach(
                lineupPlayer ->
                        players.add(
                                playerRepo.findByEspnId(lineupPlayer.getString("espnId"))
                                        .switchIfEmpty(
                                                Mono.just(
                                                        create(lineupPlayer.getString("name"),
                                                                lineupPlayer.getString("espnId"))
                                                )
                                        )
                                        .map(m -> {
                                            m.getStats().addAll(getStats(lineupPlayer));
                                            return m;
                                        })
                        )
        );

        return players;
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

    private Player create(String label, String espnId) {
        Player player = new Player();

        player.setId(UUID.randomUUID());
        player.setLabel(label);
        player.setEspnId(espnId);

        return player;
    }

   @PostConstruct
    private void initDb(){
        playerRepo.findAll().count().filter(count -> count == 0).subscribe(
                then -> Flux.fromStream(
                        IntStream.range(0, 10000).boxed()
                ).subscribe(
                        index ->
                                playerRepo.save(
                                        Player.builder()
                                                .id(UUID.randomUUID())
                                                .label("TBC")
                                                .espnId("TBC")
                                                .build()
                                ).subscribe()
                )
        );
    }
}
