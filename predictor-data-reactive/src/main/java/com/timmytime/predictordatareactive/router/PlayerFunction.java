package com.timmytime.predictordatareactive.router;

import com.timmytime.predictordatareactive.handler.PlayerHandler;
import com.timmytime.predictordatareactive.service.LineupPlayerService;
import com.timmytime.predictordatareactive.service.PlayerService;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
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

    @Bean
    @RouterOperation(beanClass = LineupPlayerService.class, beanMethod = "totalAppearances")
    RouterFunction<ServerResponse> totalAppearances(PlayerHandler playerHandler) {
        return route(RequestPredicates.GET("/players/total-appearances/{player}")
                , playerHandler::getTotalAppearances);
    }

    @Bean
    @RouterOperation(beanClass = PlayerService.class, beanMethod = "createFantasyFootballers")
    RouterFunction<ServerResponse> createFantasyFootballers(PlayerHandler playerHandler) {

        return route(RequestPredicates.POST("/create-fantasy-footballers")
                        .and(RequestPredicates.accept(MediaType.APPLICATION_STREAM_JSON)),
                playerHandler::createFantasyFootballers);
    }

    @Bean
    @RouterOperation(beanClass = PlayerService.class, beanMethod = "createGoalkeepers")
    RouterFunction<ServerResponse> createGoalkeepers(PlayerHandler playerHandler) {

        return route(RequestPredicates.POST("/create-goalkeepers")
                        .and(RequestPredicates.accept(MediaType.APPLICATION_STREAM_JSON)),
                playerHandler::createGoalkeepers);
    }
}
