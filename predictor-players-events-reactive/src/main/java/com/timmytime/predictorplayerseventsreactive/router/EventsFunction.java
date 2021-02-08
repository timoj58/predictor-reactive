package com.timmytime.predictorplayerseventsreactive.router;

import com.timmytime.predictorplayerseventsreactive.handler.EventsHandler;
import com.timmytime.predictorplayerseventsreactive.service.FantasyOutcomeService;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Component
public class EventsFunction {

    @Bean
    @RouterOperation(beanClass = FantasyOutcomeService.class, beanMethod = "topSelections")
    RouterFunction<ServerResponse> topSelections(EventsHandler eventsHandler) {
        return route(RequestPredicates.GET("/top-selections/{market}")
                , eventsHandler::topSelections);
    }
}
