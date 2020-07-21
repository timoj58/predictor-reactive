package com.timmytime.predictordatareactive.router;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.timmytime.predictordatareactive.handler.MessageHandler;
import com.timmytime.predictordatareactive.message.ResultMessage;
import com.timmytime.predictordatareactive.service.MessageReceivedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import static org.springframework.web.reactive.function.BodyExtractors.toMono;

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
    @RouterOperation(beanClass = MessageReceivedService.class, beanMethod = "completed")
    RouterFunction<ServerResponse> completed(MessageHandler messageHandler) {
        return route(RequestPredicates.POST("/completed")
                , messageHandler::completed);
    }

}
