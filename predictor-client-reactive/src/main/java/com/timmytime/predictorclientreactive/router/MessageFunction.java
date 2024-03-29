package com.timmytime.predictorclientreactive.router;

import com.timmytime.predictorclientreactive.handler.MessageHandler;
import com.timmytime.predictorclientreactive.service.MessageReceivedService;
import com.timmytime.predictorclientreactive.service.VocabService;
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
                        .and(RequestPredicates.accept(MediaType.APPLICATION_NDJSON)),
                messageHandler::receive);
    }

    @Bean
    @RouterOperation(beanClass = VocabService.class, beanMethod = "createVocab")
    RouterFunction<ServerResponse> createVocab(MessageHandler messageHandler) {

        return route(RequestPredicates.POST("/create-vocab")
                        .and(RequestPredicates.accept(MediaType.APPLICATION_NDJSON)),
                messageHandler::createVocab);
    }

}
