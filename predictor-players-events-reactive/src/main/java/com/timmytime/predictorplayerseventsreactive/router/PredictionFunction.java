package com.timmytime.predictorplayerseventsreactive.router;

import com.timmytime.predictorplayerseventsreactive.handler.PredictionHandler;
import com.timmytime.predictorplayerseventsreactive.service.PredictionService;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Component
public class PredictionFunction {

    @Bean
    @RouterOperation(beanClass = PredictionService.class, beanMethod = "fix")
    RouterFunction<ServerResponse> fix(PredictionHandler predictionHandler) {
        return route(RequestPredicates.PUT("/fix-predictions")
                , predictionHandler::fix);
    }

}
