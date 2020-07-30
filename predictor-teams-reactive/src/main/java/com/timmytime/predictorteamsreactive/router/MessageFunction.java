package com.timmytime.predictorteamsreactive.router;

import com.timmytime.predictorteamsreactive.handler.MessageHandler;
import com.timmytime.predictorteamsreactive.service.MessageReceivedService;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Component
public class MessageFunction {

    @Bean
    @RouterOperation(beanClass = MessageReceivedService.class, beanMethod = "receive")
    RouterFunction<ServerResponse> receive(MessageHandler messageHandler) {
        return route(RequestPredicates.POST("/message")
                        .and(RequestPredicates.contentType(MediaType.APPLICATION_JSON))
                , messageHandler::receive);
    }

    @Bean
    @RouterOperation(beanClass = MessageReceivedService.class, beanMethod = "training")
    RouterFunction<ServerResponse> training(MessageHandler messageHandler) {
        return route(RequestPredicates.PUT("/training")
                , messageHandler::training);
    }

    @Bean
    @RouterOperation(beanClass = MessageReceivedService.class, beanMethod = "initTraining")
    RouterFunction<ServerResponse> initTraining(MessageHandler messageHandler) {
        return route(RequestPredicates.POST("/init-training")
                , messageHandler::initTraining);
    }

}
