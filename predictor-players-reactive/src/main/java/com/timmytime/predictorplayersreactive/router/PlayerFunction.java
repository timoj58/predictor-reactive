package com.timmytime.predictorplayersreactive.router;

import com.timmytime.predictorplayersreactive.handler.PlayerHandler;
import com.timmytime.predictorplayersreactive.service.PlayerResponseService;
import com.timmytime.predictorplayersreactive.service.PlayerService;
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
    @RouterOperation(beanClass = PlayerService.class, beanMethod = "byMatch")
    RouterFunction<ServerResponse> getPlayers(PlayerHandler playerHandler) {
        return route(RequestPredicates.GET("/players/{competition}")
                , playerHandler::getPlayers);
    }

    @Bean
    @RouterOperation(beanClass = PlayerResponseService.class, beanMethod = "getPlayer")
    RouterFunction<ServerResponse> getPlayer(PlayerHandler playerHandler) {
        return route(RequestPredicates.GET("/player/{id}")
                , playerHandler::getPlayer);
    }
}