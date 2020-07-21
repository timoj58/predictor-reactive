package com.timmytime.predictordatareactive.handler;

import com.timmytime.predictordatareactive.model.Match;
import com.timmytime.predictordatareactive.model.Player;
import com.timmytime.predictordatareactive.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class MatchHandler {

    private final MatchService matchService;

    @Autowired
    public MatchHandler(
            MatchService matchService
    ){
        this.matchService = matchService;
    }

    public Mono<ServerResponse> getMatches(ServerRequest serverRequest){

        return ServerResponse.ok().body(
                matchService.getMatches(UUID.fromString(
                        serverRequest.pathVariable("team"))
                ),
                Match.class
        );
    }

    public Mono<ServerResponse> getMatchesByCountry(ServerRequest serverRequest){

        return ServerResponse.ok().body(
                matchService.getMatchesByCountry(
                        serverRequest.pathVariable("country"),
                        serverRequest.pathVariable("fromDate"),
                        serverRequest.pathVariable("toDate")
                        ),
                Match.class
        );
    }
}
