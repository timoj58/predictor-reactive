package com.timmytime.predictorplayersreactive.service.impl;

import com.timmytime.predictorplayersreactive.facade.WebClientFacade;
import com.timmytime.predictorplayersreactive.model.LineupPlayer;
import com.timmytime.predictorplayersreactive.model.Match;
import com.timmytime.predictorplayersreactive.model.PlayerMatch;
import com.timmytime.predictorplayersreactive.model.StatMetric;
import com.timmytime.predictorplayersreactive.service.PlayerMatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Consumer;

@Service("playerMatchService")
public class PlayerMatchServiceImpl implements PlayerMatchService {

    private final Logger log = LoggerFactory.getLogger(PlayerMatchServiceImpl.class);

    private final WebClientFacade webClientFacade;
    private final String dataHost;

    @Autowired
    public PlayerMatchServiceImpl(
            @Value("${data.host}") String dataHost,
            WebClientFacade webClientFacade
    ) {
        this.dataHost = dataHost;
        this.webClientFacade = webClientFacade;
    }


    @Override
    public Flux<LineupPlayer> getAppearances(UUID player, String fromDate, String toDate) {
        return webClientFacade.getAppearances(
                dataHost + "/players/appearances/" + player + "?fromDate=" + fromDate + "&toDate=" + toDate
        );
    }

    @Override
    public Mono<Match> getMatch(UUID match) {
        return webClientFacade.getMatch(dataHost + "/match/" + match);
    }

    @Override
    public Flux<StatMetric> getStats(UUID match, UUID player) {
        return webClientFacade.getStats(dataHost + "/stats/" + player + "/" + match);
    }

    @Override
    public void create(
            UUID player,
            String fromDate,
            String toDate,
            Consumer<PlayerMatch> consumer) {

        log.info("creating {}", player);

        getAppearances(player,
                fromDate,
                toDate
        )
                .delayElements(Duration.ofMillis(4))
                .subscribe(appearance ->
                        getMatch(appearance.getMatchId())
                                .subscribe(match -> {
                                            PlayerMatch playerMatch =
                                                    PlayerMatch.builder()
                                                            .date(match.getDate())
                                                            .playerId(player)
                                                            .opponent(appearance.getTeamId().equals(match.getHomeTeam()) ? match.getAwayTeam() : match.getHomeTeam())
                                                            .home(appearance.getTeamId().equals(match.getHomeTeam()) ? Boolean.TRUE : Boolean.FALSE)
                                                            .minutes(appearance.getAppearance() < 0 ? 0 : appearance.getAppearance() > 90 ? 90 : appearance.getAppearance())
                                                            .stats(new ArrayList<>())
                                                            .conceded(appearance.getTeamId().equals(match.getHomeTeam()) ? match.getAwayScore() : match.getHomeScore())
                                                            .build();

                                            getStats(match.getId(), player)
                                                    .doOnNext(stat -> playerMatch.getStats().add(stat))
                                                    .doFinally(save ->
                                                            consumer.accept(playerMatch)
                                                    )
                                                    .subscribe();
                                        }
                                )
                );
    }


}
