package com.timmytime.predictordatareactive.handler;

import com.timmytime.predictordatareactive.model.Team;
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

    public Mono<ServerResponse> alias(ServerRequest request) {

        Optional<Team> team = teamService.getTeam(
                request.pathVariable("alias"),
                request.queryParam("country").get()
        );

        return team.isPresent() ?
                ServerResponse.ok().bodyValue(team.get())
                :
                ServerResponse.notFound().build();

    }

    public Mono<ServerResponse> findByCountry(ServerRequest serverRequest) {

        return ServerResponse.ok().bodyValue(
                teamService.getTeams(serverRequest.pathVariable("country"))
        );
    }

    public Mono<ServerResponse> loadNewTeams(ServerRequest request) {

        return ServerResponse.ok().build(
                teamService.loadNewTeams()
        );
    }

}
