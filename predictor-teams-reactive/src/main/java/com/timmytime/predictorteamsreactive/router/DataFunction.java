package com.timmytime.predictorteamsreactive.router;

import com.timmytime.predictorteamsreactive.handler.DataHandler;
import com.timmytime.predictorteamsreactive.service.TensorflowDataService;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Component
public class DataFunction {

    @Bean
    @RouterOperation(beanClass = TensorflowDataService.class, beanMethod = "getCountryCsv")
    RouterFunction<ServerResponse> getData(DataHandler dataHandler) {
        return route(RequestPredicates.GET("/data/{country}/{fromDate}/{toDate}")
                , dataHandler::getData);
    }
}
