package com.timmytime.predictordatareactive.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.timmytime.predictordatareactive.model.Team;
import com.timmytime.predictordatareactive.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class TeamHandler {

    private final TeamService teamService;

    @Autowired
    public TeamHandler(
            TeamService teamService
    ){
        this.teamService = teamService;
    }

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

    public Mono<ServerResponse> findByCountry(ServerRequest serverRequest){

        return ServerResponse.ok().bodyValue(
                teamService.getTeams(serverRequest.pathVariable("country"))
        );
    }

}
