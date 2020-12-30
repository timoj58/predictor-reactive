package com.timmytime.predictordatareactive.handler;

import com.timmytime.predictordatareactive.model.LineupPlayer;
import com.timmytime.predictordatareactive.model.Player;
import com.timmytime.predictordatareactive.service.LineupPlayerService;
import com.timmytime.predictordatareactive.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class PlayerHandler {

    private final PlayerService playerService;
    private final LineupPlayerService lineupPlayerService;

    public Mono<ServerResponse> getByCompetition(ServerRequest serverRequest) {

        return ServerResponse.ok().body(
                playerService.findByCompetition(
                        serverRequest.pathVariable("competition"),
                        serverRequest.queryParam("date").get(),
                        Boolean.valueOf(serverRequest.queryParam("fantasy").get())),
                Player.class
        );
    }

    public Mono<ServerResponse> findFantasyFootballers(ServerRequest serverRequest) {

        return ServerResponse.ok().body(
                playerService.findFantasyFootballers(),
                Player.class
        );
    }

    public Mono<ServerResponse> getAppearances(ServerRequest serverRequest) {

        return ServerResponse.ok().body(
                lineupPlayerService.find(
                        UUID.fromString(serverRequest.pathVariable("player")),
                        serverRequest.queryParam("fromDate").get(),
                        serverRequest.queryParam("toDate").get()
                ),
                LineupPlayer.class
        );
    }

    public Mono<ServerResponse> getTotalAppearances(ServerRequest serverRequest) {

        return ServerResponse.ok().body(
                lineupPlayerService.totalAppearances(
                        UUID.fromString(serverRequest.pathVariable("player"))
                ),
                Integer.class
        );
    }

    public Mono<ServerResponse> createFantasyFootballers(ServerRequest request) {

        return ServerResponse.ok().build(
                playerService.createFantasyFootballers()
        );
    }

    public Mono<ServerResponse> createGoalkeepers(ServerRequest request) {

        return ServerResponse.ok().build(
                playerService.createGoalkeepers()
        );
    }


}
