package com.timmytime.predictorplayersreactive.handler;

import com.timmytime.predictorplayersreactive.service.PlayerResponseService;
import com.timmytime.predictorplayersreactive.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class PlayerHandler {
    private final PlayerService playerService;
    private final PlayerResponseService playerResponseService;

    @Autowired
    public PlayerHandler(
            PlayerService playerService,
            PlayerResponseService playerResponseService
    ){
        this.playerService = playerService;
        this.playerResponseService = playerResponseService;
    }

    public Mono<ServerResponse> getPlayers(ServerRequest request) {

        return ServerResponse.ok().bodyValue(
                playerService.byMatch(
                        request.pathVariable("competition"),
                        UUID.fromString(request.queryParam("home").get()),
                        UUID.fromString(request.queryParam("away").get()))
        );
    }

    public Mono<ServerResponse> getPlayer(ServerRequest request) {

        return ServerResponse.ok().bodyValue(
                playerResponseService.getPlayer(
                        UUID.fromString(request.pathVariable("id"))
                )
        );
    }
}
