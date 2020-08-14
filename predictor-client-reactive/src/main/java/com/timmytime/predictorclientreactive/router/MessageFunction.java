package com.timmytime.predictorclientreactive.router;

import com.timmytime.predictorclientreactive.handler.MessageHandler;
import com.timmytime.predictorclientreactive.service.MessageReceivedService;
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
    @RouterOperation(beanClass = MessageReceivedService.class, beanMethod = "test")
    RouterFunction<ServerResponse> scrape(MessageHandler messageHandler) {

        return route(RequestPredicates.POST("/test")
                        .and(RequestPredicates.accept(MediaType.APPLICATION_STREAM_JSON)),
                messageHandler::test);
    }
}
