package com.timmytime.predictorplayerseventsreactive.router;

import com.timmytime.predictorplayerseventsreactive.handler.PredictionHandler;
import com.timmytime.predictorplayerseventsreactive.service.TensorflowPredictionService;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
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
                        .and(RequestPredicates.contentType(MediaType.APPLICATION_JSON))
                , predictionHandler::predict);
    }
}
