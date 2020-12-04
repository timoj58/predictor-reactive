package com.timmytime.predictoreventsreactive.router;

import com.timmytime.predictoreventsreactive.handler.EventsHandler;
import com.timmytime.predictoreventsreactive.service.EventOutcomeService;
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
    @RouterOperation(beanClass = EventOutcomeService.class, beanMethod = "currentEvents")
    RouterFunction<ServerResponse> events(EventsHandler eventsHandler) {
        return route(RequestPredicates.GET("/events/{competition}")
                , eventsHandler::currentEvents);
    }

    @Bean
    @RouterOperation(beanClass = EventOutcomeService.class, beanMethod = "previousEvents")
    RouterFunction<ServerResponse> previousEvents(EventsHandler eventsHandler) {
        return route(RequestPredicates.GET("/previous-events/{competition}")
                , eventsHandler::previousEvents);
    }

    @Bean
    @RouterOperation(beanClass = EventOutcomeService.class, beanMethod = "previousEventsByTeam")
    RouterFunction<ServerResponse> previousEventsByTeam(EventsHandler eventsHandler) {
        return route(RequestPredicates.GET("/previous-events-by-team/{team}")
                , eventsHandler::previousEventsByTeam);
    }
}
