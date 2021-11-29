package com.timmytime.predictorplayersreactive.service.impl;

import com.timmytime.predictorplayersreactive.facade.WebClientFacade;
import com.timmytime.predictorplayersreactive.model.LineupPlayer;
import com.timmytime.predictorplayersreactive.model.Match;
import com.timmytime.predictorplayersreactive.model.PlayerMatch;
import com.timmytime.predictorplayersreactive.model.StatMetric;
import com.timmytime.predictorplayersreactive.service.PlayerMatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
@Service("playerMatchService")
public class PlayerMatchServiceImpl implements PlayerMatchService {

    private final WebClientFacade webClientFacade;
    private final String dataHost;

    @Autowired
    public PlayerMatchServiceImpl(
            @Value("${clients.data}") String dataHost,
            WebClientFacade webClientFacade
    ) {
        this.dataHost = dataHost;
        this.webClientFacade = webClientFacade;
    }


    @Override
    public Flux<LineupPlayer> getAppearances(UUID player, Optional<LocalDate> date) {
        var url = new StringBuilder(dataHost + "/players/appearances/" + player);

        date.ifPresent(then ->
                url.append("?date="+then.format(
                        DateTimeFormatter.ofPattern("dd-MM-yyyy")
                )));

        return webClientFacade.getAppearances(url.toString());
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
    public void create(UUID player, Consumer<PlayerMatch> consumer) {

        log.info("creating {}", player);
        getAppearances(player, Optional.empty())
                .limitRate(1)
                .subscribe(appearance -> processPlayerMatch(
                        appearance, consumer
                        )
                );
    }

    @Override
    public void next(UUID player, LocalDate date, Consumer<PlayerMatch> consumer) {
        getAppearances(player, Optional.of(date))
                .filter(f -> f.getDate().toLocalDate().isAfter(date))
                .limitRate(10)
                .subscribe(appearance -> processPlayerMatch(
                        appearance, consumer
                ));

    }

    private void processPlayerMatch(LineupPlayer appearance, Consumer<PlayerMatch> consumer) {
        getMatch(appearance.getMatchId())
                .subscribe(match -> {
                            PlayerMatch playerMatch =
                                    PlayerMatch.builder()
                                            .date(match.getDate().toLocalDate())
                                            .playerId(appearance.getPlayer())
                                            .opponent(appearance.getTeamId().equals(match.getHomeTeam()) ? match.getAwayTeam() : match.getHomeTeam())
                                            .home(appearance.getTeamId().equals(match.getHomeTeam()) ? Boolean.TRUE : Boolean.FALSE)
                                            .stats(new ArrayList<>())
                                            .build();

                            getStats(match.getId(), appearance.getPlayer())
                                    .limitRate(1)
                                    .doOnNext(stat -> playerMatch.getStats().add(stat))
                                    .doFinally(save ->
                                            consumer.accept(playerMatch)
                                    )
                                    .subscribe();
                        }
                );
    }


}
