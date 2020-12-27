package com.timmytime.predictorplayerseventsreactive.handler;

import com.timmytime.predictorplayerseventsreactive.service.TensorflowDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class DataHandler {

    private final TensorflowDataService tensorflowDataService;

    public Mono<ServerResponse> getData(ServerRequest request) {

        return ServerResponse.ok().bodyValue(
                tensorflowDataService.getPlayerCsv(
                        request.pathVariable("fromDate"),
                        request.pathVariable("toDate")
                )
        );
    }
}
