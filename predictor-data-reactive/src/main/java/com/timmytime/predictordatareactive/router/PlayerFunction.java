package com.timmytime.predictordatareactive.router;

import com.timmytime.predictordatareactive.handler.PlayerHandler;
import com.timmytime.predictordatareactive.service.LineupPlayerService;
import com.timmytime.predictordatareactive.service.PlayerService;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Component
public class PlayerFunction {

    @Bean
    @RouterOperation(beanClass = PlayerService.class, beanMethod = "findByCompetition")
    RouterFunction<ServerResponse> getPlayersByCompetition(PlayerHandler playerHandler) {
        return route(RequestPredicates.GET("/players/competition/{competition}")
                , playerHandler::getByCompetition);
    }

    @Bean
    @RouterOperation(beanClass = PlayerService.class, beanMethod = "findFantasyFootballers")
    RouterFunction<ServerResponse> findFantasyFootballers(PlayerHandler playerHandler) {
        return route(RequestPredicates.GET("/players/fantasy")
                , playerHandler::findFantasyFootballers);
    }

    @Bean
    @RouterOperation(beanClass = LineupPlayerService.class, beanMethod = "find")
    RouterFunction<ServerResponse> getAppearances(PlayerHandler playerHandler) {
        return route(RequestPredicates.GET("/players/appearances/{player}")
                , playerHandler::getAppearances);
    }
}
