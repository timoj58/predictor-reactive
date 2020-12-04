package com.timmytime.predictorplayersreactive.handler;

import com.timmytime.predictorplayersreactive.service.TensorflowDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class DataHandler {

    private final TensorflowDataService tensorflowDataService;

    @Autowired
    public DataHandler(
            TensorflowDataService tensorflowDataService
    ) {
        this.tensorflowDataService = tensorflowDataService;
    }

    public Mono<ServerResponse> getData(ServerRequest request) {

        return ServerResponse.ok().bodyValue(
                tensorflowDataService.getPlayerCsv(
                        request.pathVariable("fromDate"),
                        request.pathVariable("toDate")
                )
        );
    }
}
