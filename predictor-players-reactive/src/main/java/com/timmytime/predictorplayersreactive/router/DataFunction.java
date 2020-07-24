package com.timmytime.predictorplayersreactive.router;

import com.timmytime.predictorplayersreactive.handler.DataHandler;
import com.timmytime.predictorplayersreactive.handler.MessageHandler;
import com.timmytime.predictorplayersreactive.service.MessageReceivedService;
import com.timmytime.predictorplayersreactive.service.TensorflowDataService;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Component
public class DataFunction {

    @Bean
    @RouterOperation(beanClass = TensorflowDataService.class, beanMethod = "getPlayerCsv")
    RouterFunction<ServerResponse> getData(DataHandler dataHandler) {
        return route(RequestPredicates.GET("/data")
                , dataHandler::getData);
    }
}
