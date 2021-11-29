package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.facade.WebClientFacade;
import com.timmytime.predictorplayerseventsreactive.model.Player;
import com.timmytime.predictorplayerseventsreactive.service.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service("playerService")
public class PlayerServiceImpl implements PlayerService {

    private final WebClientFacade webClientFacade;
    private final String dataHost;

    @Autowired
    public PlayerServiceImpl(
            @Value("${clients.data}") String dataHost,
            WebClientFacade webClientFacade
    ) {
        this.dataHost = dataHost;
        this.webClientFacade = webClientFacade;
    }


    @Override
    public Flux<Player> get(String competition) {
        return webClientFacade.getPlayers(dataHost + "/players/competition/" + competition
                + "?date=" + LocalDate.now().minusYears(2).format(
                DateTimeFormatter.ofPattern("dd-MM-yyyy")
        ));
    }

    @Override
    public Flux<Player> get(String competition, UUID team) {
        return get(competition).filter(f -> f.getLatestTeam().equals(team));
    }

    @Override
    public Mono<Player> get(UUID id) {
        return webClientFacade.getPlayer(dataHost + "/players/" + id.toString());
    }

    @Override
    public Flux<Player> get() {
        return webClientFacade.getPlayers(dataHost + "/players")
                .filter(f -> f.getLastAppearance() != null && f.getLastAppearance().isAfter(
                        LocalDate.now().minusYears(2)
                ));
    }

    @Override
    public Flux<Player> byMatch(String competition, UUID home, UUID away) {

        Flux<Player> homePlayers = get(competition, home);
        Flux<Player> awayPlayers = get(competition, away);

        return Flux.concat(homePlayers, awayPlayers);
    }
}
