package com.timmytime.predictorteamsreactive.router;

import com.timmytime.predictorteamsreactive.handler.InitHandler;
import com.timmytime.predictorteamsreactive.service.InitService;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Component
public class InitFunction {

    @Bean
    @RouterOperation(beanClass = InitService.class, beanMethod = "init")
    RouterFunction<ServerResponse> init(InitHandler initHandler) {
        return route(RequestPredicates.POST("/init")
                , initHandler::init);
    }
}
