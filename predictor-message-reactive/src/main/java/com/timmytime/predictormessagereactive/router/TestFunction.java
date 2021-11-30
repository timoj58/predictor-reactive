package com.timmytime.predictormessagereactive.router;

import com.timmytime.predictormessagereactive.handler.TestHandler;
import com.timmytime.predictormessagereactive.service.OrchestrationService;
import com.timmytime.predictormessagereactive.service.TestApiService;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Component
public class TestFunction {
    @Bean
    @RouterOperation(beanClass = TestApiService.class, beanMethod = "trainTeams")
    RouterFunction<ServerResponse> trainTeamResults(TestHandler testHandler) {
        return route(RequestPredicates.POST("/teams/train/results/{country}/{from}/{to}/{receipt}")
                , testHandler::trainTeams);
    }

    @Bean
    @RouterOperation(beanClass = TestApiService.class, beanMethod = "trainTeams")
    RouterFunction<ServerResponse> trainTeamGoals(TestHandler testHandler) {
        return route(RequestPredicates.POST("/teams/train/goals/{country}/{from}/{to}/{receipt}")
                , testHandler::trainTeams);
    }

    @Bean
    @RouterOperation(beanClass = TestApiService.class, beanMethod = "predictTeams")
    RouterFunction<ServerResponse> predictTeamResults(TestHandler testHandler) {
        return route(RequestPredicates.POST("/teams/predict/result/{country}/{receipt}")
                        .and(RequestPredicates.contentType(MediaType.APPLICATION_JSON))
                , testHandler::predictTeamResult);
    }

    @Bean
    @RouterOperation(beanClass = TestApiService.class, beanMethod = "predictTeams")
    RouterFunction<ServerResponse> predictTeamGoals(TestHandler testHandler) {
        return route(RequestPredicates.POST("/teams/predict/goals/{country}/{receipt}")
                        .and(RequestPredicates.contentType(MediaType.APPLICATION_JSON))
                , testHandler::predictTeamGoals);
    }

    @Bean
    @RouterOperation(beanClass = TestApiService.class, beanMethod = "trainPlayers")
    RouterFunction<ServerResponse> trainPlayersGoals(TestHandler testHandler) {
        return route(RequestPredicates.POST("/players/train/goals/{from}/{to}/{receipt}")
                , testHandler::trainPlayers);
    }

    @Bean
    @RouterOperation(beanClass = TestApiService.class, beanMethod = "trainPlayers")
    RouterFunction<ServerResponse> trainPlayersAssists(TestHandler testHandler) {
        return route(RequestPredicates.POST("/players/train/assists/{from}/{to}/{receipt}")
                , testHandler::trainPlayers);
    }

    @Bean
    @RouterOperation(beanClass = TestApiService.class, beanMethod = "trainPlayers")
    RouterFunction<ServerResponse> trainPlayersCards(TestHandler testHandler) {
        return route(RequestPredicates.POST("/players/train/yellow-card/{from}/{to}/{receipt}")
                , testHandler::trainPlayers);
    }

    @Bean
    @RouterOperation(beanClass = TestApiService.class, beanMethod = "playerConfig")
    RouterFunction<ServerResponse> playerInit(TestHandler testHandler) {
        return route(RequestPredicates.PUT("/players/predict/init/{type}")
                , testHandler::playerConfig);
    }

    @Bean
    @RouterOperation(beanClass = TestApiService.class, beanMethod = "playerConfig")
    RouterFunction<ServerResponse> playerDestroy(TestHandler testHandler) {
        return route(RequestPredicates.PUT("/players/predict/clear-down/{type}")
                , testHandler::playerConfig);
    }

    @Bean
    @RouterOperation(beanClass = TestApiService.class, beanMethod = "predictTeams")
    RouterFunction<ServerResponse> predictPlayerGoals(TestHandler testHandler) {
        return route(RequestPredicates.POST("/players/predict/goals/{init}/{receipt}")
                        .and(RequestPredicates.contentType(MediaType.APPLICATION_JSON))
                , testHandler::predictPlayer);
    }

    @Bean
    @RouterOperation(beanClass = TestApiService.class, beanMethod = "predictTeams")
    RouterFunction<ServerResponse> predictPlayerAssists(TestHandler testHandler) {
        return route(RequestPredicates.POST("/players/predict/assists/{init}/{receipt}")
                        .and(RequestPredicates.contentType(MediaType.APPLICATION_JSON))
                , testHandler::predictPlayer);
    }

    @Bean
    @RouterOperation(beanClass = TestApiService.class, beanMethod = "predictTeams")
    RouterFunction<ServerResponse> predictPlayerCards(TestHandler testHandler) {
        return route(RequestPredicates.POST("/players/predict/yellow-card/{init}/{receipt}")
                        .and(RequestPredicates.contentType(MediaType.APPLICATION_JSON))
                , testHandler::predictPlayer);
    }

    @Bean
    @RouterOperation(beanClass = TestApiService.class, beanMethod = "uploadFile")
    RouterFunction<ServerResponse> uploadFile(TestHandler testHandler) {
        return route(RequestPredicates.POST("/file")
                        .and(RequestPredicates.contentType(MediaType.APPLICATION_JSON))
                , testHandler::uploadFile);
    }

    @Bean
    @RouterOperation(beanClass = OrchestrationService.class, beanMethod = "testStatus")
    RouterFunction<ServerResponse> testStatus(TestHandler testHandler) {
        return route(RequestPredicates.GET("/status/{action}")
                , testHandler::testStatus);
    }

}
