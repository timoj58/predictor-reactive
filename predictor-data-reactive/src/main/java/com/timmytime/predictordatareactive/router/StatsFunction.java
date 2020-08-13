package com.timmytime.predictordatareactive.router;

import com.timmytime.predictordatareactive.handler.StatsHandler;
import com.timmytime.predictordatareactive.service.StatMetricService;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Component
public class StatsFunction {

    @Bean
    @RouterOperation(beanClass = StatMetricService.class, beanMethod = "find")
    RouterFunction<ServerResponse> getStats(StatsHandler statsHandler) {
        return route(RequestPredicates.GET("/stats/{player}/{match}")
                , statsHandler::getMatchStats);
    }

    @Bean
    @RouterOperation(beanClass = StatMetricService.class, beanMethod = "getPlayerStats")
    RouterFunction<ServerResponse> getAllStats(StatsHandler statsHandler) {
        return route(RequestPredicates.GET("/all-stats/{player}")
                , statsHandler::getPlayerStats);
    }
}
