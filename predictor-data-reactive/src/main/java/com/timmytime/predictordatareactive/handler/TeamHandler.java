package com.timmytime.predictordatareactive.handler;

import com.timmytime.predictordatareactive.model.Team;
import com.timmytime.predictordatareactive.response.MatchTeams;
import com.timmytime.predictordatareactive.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class TeamHandler {

    private final TeamService teamService;

    public Mono<ServerResponse> findByCountry(ServerRequest serverRequest) {

        return ServerResponse.ok().body(
                teamService.getTeamsFlux(serverRequest.pathVariable("country")),
                Team.class
        );
    }

    public Mono<ServerResponse> getMatchTeams(ServerRequest serverRequest) {

        return ServerResponse.ok().body(
                teamService.getMatchTeams(serverRequest.pathVariable("competition"),
                        serverRequest.queryParam("home").get(),
                        serverRequest.queryParam("away").get()),
                MatchTeams.class
        );
    }

}
