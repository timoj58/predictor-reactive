package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.enumerator.ApplicableFantasyLeagues;
import com.timmytime.predictorplayerseventsreactive.facade.WebClientFacade;
import com.timmytime.predictorplayerseventsreactive.model.Player;
import com.timmytime.predictorplayerseventsreactive.service.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("playerService")
public class PlayerServiceImpl implements PlayerService {

    private final WebClientFacade webClientFacade;
    private final String dataHost;

    private final Map<String, List<Player>> players = new HashMap<>();

    @Autowired
    public PlayerServiceImpl(
            @Value("${clients.data}") String dataHost,
            WebClientFacade webClientFacade
    ) {
        this.dataHost = dataHost;
        this.webClientFacade = webClientFacade;
    }

    @PostConstruct
    @Override
    public void load() {

        log.info("loading players");

        //only interested in players active in the last two years
        String date = LocalDate.now().minusYears(2).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        Arrays.stream(
                ApplicableFantasyLeagues.values()
        )
                .forEach(league -> {

                    players.put(league.name().toLowerCase(), new ArrayList<>());
                    webClientFacade.getPlayers(dataHost
                            + "/players/competition/"
                            + league.name().toLowerCase()
                            + "?date=" + date + "&fantasy=true")
                            .subscribe(player -> players.get(league.name().toLowerCase()).add(player));
                });


    }

    @Override
    public List<Player> get(String competition) {
        return players.get(competition);
    }

    @Override
    public List<Player> get(String competition, UUID team) {
        return players.get(competition)
                .stream()
                .filter(f -> f.getLatestTeam().equals(team))
                .collect(Collectors.toList());
    }

    @Override
    public Player get(UUID id) {
        return get().stream().filter(f -> f.getId().equals(id)).findFirst().get();
    }

    @Override
    public List<Player> get() {
        return players.values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public Flux<Player> byMatch(String competition, UUID home, UUID away) {

        Flux<Player> homePlayers = Flux.fromStream(
                players.get(competition)
                        .stream()
                        .filter(f -> f.getLatestTeam().equals(home)));

        Flux<Player> awayPlayers = Flux.fromStream(
                players.get(competition)
                        .stream()
                        .filter(f -> f.getLatestTeam().equals(away)));

        return Flux.concat(homePlayers, awayPlayers);
    }
}
