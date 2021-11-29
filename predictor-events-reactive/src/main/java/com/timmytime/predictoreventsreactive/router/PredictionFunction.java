package com.timmytime.predictoreventsreactive.router;

import com.timmytime.predictoreventsreactive.handler.PredictionHandler;
import com.timmytime.predictoreventsreactive.service.TensorflowPredictionService;
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
    @RouterOperation(beanClass = TensorflowPredictionService.class, beanMethod = "predict")
    RouterFunction<ServerResponse> predict(PredictionHandler predictionHandler) {
        return route(RequestPredicates.POST("/predict")
                , predictionHandler::predict);
    }

}
