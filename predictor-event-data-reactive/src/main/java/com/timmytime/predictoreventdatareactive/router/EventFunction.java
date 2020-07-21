package com.timmytime.predictoreventdatareactive.router;

import com.timmytime.predictoreventdatareactive.handler.EventHandler;
import com.timmytime.predictoreventdatareactive.handler.MessageHandler;
import com.timmytime.predictoreventdatareactive.service.EventOddsService;
import com.timmytime.predictoreventdatareactive.service.MessageReceivedService;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Component
public class EventFunction {

    @Bean
    @RouterOperation(beanClass = EventOddsService.class, beanMethod = "getEvents")
    RouterFunction<ServerResponse> events(EventHandler eventHandler) {
        return route(RequestPredicates.GET("/events/{competition}")
                , eventHandler::events);
    }
}
