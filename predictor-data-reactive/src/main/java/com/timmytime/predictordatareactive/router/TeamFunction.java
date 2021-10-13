package com.timmytime.predictordatareactive.router;

import com.timmytime.predictordatareactive.handler.TeamHandler;
import com.timmytime.predictordatareactive.service.TeamService;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Component
public class TeamFunction {

    @Bean
    @RouterOperation(beanClass = TeamService.class, beanMethod = "getTeams")
    RouterFunction<ServerResponse> getTeamsByCountry(TeamHandler teamHandler) {
        return route(RequestPredicates.GET("/teams/country/{country}")
                , teamHandler::findByCountry);
    }

}
