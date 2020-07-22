package com.timmytime.predictordatareactive.router;

import com.timmytime.predictordatareactive.handler.MatchHandler;
import com.timmytime.predictordatareactive.handler.TeamHandler;
import com.timmytime.predictordatareactive.service.MatchService;
import com.timmytime.predictordatareactive.service.TeamService;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Component
public class MatchFunction {

    @Bean
    @RouterOperation(beanClass = MatchService.class, beanMethod = "getMatch")
    RouterFunction<ServerResponse> getMatch(MatchHandler matchHandler) {
        return route(RequestPredicates.GET("/match")
                , matchHandler::getMatch);
    }


    @Bean
    @RouterOperation(beanClass = MatchService.class, beanMethod = "getMatches")
    RouterFunction<ServerResponse> getMatches(MatchHandler matchHandler) {
        return route(RequestPredicates.GET("/match/team/{team}")
                , matchHandler::getMatches);
    }

    @Bean
    @RouterOperation(beanClass = MatchService.class, beanMethod = "getMatchesByCountry")
    RouterFunction<ServerResponse> getMatchesByCountry(MatchHandler matchHandler) {
        return route(RequestPredicates.GET("/match/country/{country}/{fromDate}/{toDate}")
                , matchHandler::getMatchesByCountry);
    }
}
