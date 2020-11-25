package com.timmytime.predictorplayersreactive.service.impl;

import com.timmytime.predictorplayersreactive.enumerator.ApplicableFantasyLeagues;
import com.timmytime.predictorplayersreactive.facade.WebClientFacade;
import com.timmytime.predictorplayersreactive.model.Player;
import com.timmytime.predictorplayersreactive.service.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service("playerService")
public class PlayerServiceImpl implements PlayerService {

    private final Logger log = LoggerFactory.getLogger(PlayerServiceImpl.class);

    private final WebClientFacade webClientFacade;
    private final String dataHost;

    private final Map<String, List<Player>> players = new HashMap<>();

    @Autowired
    public PlayerServiceImpl(
            @Value("${data.host}") String dataHost,
            WebClientFacade webClientFacade
    ){
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
                            + "?date="+date+"&fantasy=true")
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
