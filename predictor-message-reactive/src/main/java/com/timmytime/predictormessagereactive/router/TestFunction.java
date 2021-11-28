package com.timmytime.predictormessagereactive.router;

import com.timmytime.predictormessagereactive.handler.TestHandler;
import com.timmytime.predictormessagereactive.service.TrainingTestService;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Component
public class TestFunction {
    @Bean
    @RouterOperation(beanClass = TrainingTestService.class, beanMethod = "trainTeams")
    RouterFunction<ServerResponse> trainTeamResults(TestHandler testHandler) {
        return route(RequestPredicates.POST("/teams/train/results/{country}/{from}/{to}/{receipt}")
                , testHandler::trainTeams);
    }

    @Bean
    @RouterOperation(beanClass = TrainingTestService.class, beanMethod = "trainTeams")
    RouterFunction<ServerResponse> trainTeamGoals(TestHandler testHandler) {
        return route(RequestPredicates.POST("/teams/train/goals/{country}/{from}/{to}/{receipt}")
                , testHandler::trainTeams);
    }

    @Bean
    @RouterOperation(beanClass = TrainingTestService.class, beanMethod = "trainPlayers")
    RouterFunction<ServerResponse> trainPlayersGoals(TestHandler testHandler) {
        return route(RequestPredicates.POST("/players/train/goals/{from}/{to}/{receipt}")
                , testHandler::trainPlayers);
    }

    @Bean
    @RouterOperation(beanClass = TrainingTestService.class, beanMethod = "trainPlayers")
    RouterFunction<ServerResponse> trainPlayersAssists(TestHandler testHandler) {
        return route(RequestPredicates.POST("/players/train/assists/{from}/{to}/{receipt}")
                , testHandler::trainPlayers);
    }

    @Bean
    @RouterOperation(beanClass = TrainingTestService.class, beanMethod = "trainPlayers")
    RouterFunction<ServerResponse> trainPlayersCards(TestHandler testHandler) {
        return route(RequestPredicates.POST("/players/train/yellow-card/{from}/{to}/{receipt}")
                , testHandler::trainPlayers);
    }

}
