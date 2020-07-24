package com.timmytime.predictordatareactive.router;

import com.timmytime.predictordatareactive.handler.MatchHandler;
import com.timmytime.predictordatareactive.service.MatchService;
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
    @RouterOperation(beanClass = MatchService.class, beanMethod = "getMatchByOpponent")
    RouterFunction<ServerResponse> getMatchByOpponent(MatchHandler matchHandler) {
        return route(RequestPredicates.GET("/match/opponent/{opponent}")
                , matchHandler::getMatchByOpponent);
    }

    @Bean
    @RouterOperation(beanClass = MatchService.class, beanMethod = "getMatch")
    RouterFunction<ServerResponse> getMatchByTeams(MatchHandler matchHandler) {
        return route(RequestPredicates.GET("/match")
                , matchHandler::getMatchByTeams);
    }

    @Bean
    @RouterOperation(beanClass = MatchService.class, beanMethod = "find")
    RouterFunction<ServerResponse> getMatch(MatchHandler matchHandler) {
        return route(RequestPredicates.GET("/match/{id}")
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
