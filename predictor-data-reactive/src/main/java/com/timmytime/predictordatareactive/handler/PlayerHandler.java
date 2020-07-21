package com.timmytime.predictordatareactive.handler;

import com.timmytime.predictordatareactive.model.Player;
import com.timmytime.predictordatareactive.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class PlayerHandler {

    private final PlayerService playerService;

    @Autowired
    public PlayerHandler(
            PlayerService playerService
    ){
        this.playerService = playerService;
    }

    public Mono<ServerResponse> getByCompetition(ServerRequest serverRequest){

        return ServerResponse.ok().body(
                playerService.findByCompetition(
                        serverRequest.pathVariable("competition"),
                        serverRequest.queryParam("date").get()),
                Player.class
        );
    }
}
