package com.timmytime.predictorplayerseventsreactive.handler;

import com.timmytime.predictorplayerseventsreactive.model.Player;
import com.timmytime.predictorplayerseventsreactive.service.PlayerService;
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

    public Mono<ServerResponse> getPlayers(ServerRequest request) {

        return ServerResponse.ok().body(
                playerService.byMatch(
                        request.pathVariable("competition"),
                        UUID.fromString(request.queryParam("home").get()),
                        UUID.fromString(request.queryParam("away").get()))
                , Player.class);
    }

}
