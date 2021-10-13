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
                        serverRequest.queryParam("date").get()),
                Player.class
        );
    }


    public Mono<ServerResponse> getAppearances(ServerRequest serverRequest) {

        return ServerResponse.ok().body(
                lineupPlayerService.find(
                        UUID.fromString(serverRequest.pathVariable("player"))
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

}
