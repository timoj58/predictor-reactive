package com.timmytime.predictorplayersreactive.router;

import com.timmytime.predictorplayersreactive.handler.MessageHandler;
import com.timmytime.predictorplayersreactive.handler.PredictionHandler;
import com.timmytime.predictorplayersreactive.service.MessageReceivedService;
import com.timmytime.predictorplayersreactive.service.PredictionService;
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
    @RouterOperation(beanClass = PredictionService.class, beanMethod = "fix")
    RouterFunction<ServerResponse> fix(PredictionHandler predictionHandler) {
        return route(RequestPredicates.POST("/fix-predictions")
                        .and(RequestPredicates.contentType(MediaType.APPLICATION_JSON))
                , predictionHandler::fix);
    }
}
