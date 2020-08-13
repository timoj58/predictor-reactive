package com.timmytime.predictordatareactive.handler;

import com.timmytime.predictordatareactive.model.Match;
import com.timmytime.predictordatareactive.model.StatMetric;
import com.timmytime.predictordatareactive.service.StatMetricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class StatsHandler {

    private final StatMetricService statMetricService;

    @Autowired
    public StatsHandler(
            StatMetricService statMetricService
    ){
        this.statMetricService = statMetricService;
    }

    public Mono<ServerResponse> getMatchStats(ServerRequest serverRequest){

        return ServerResponse.ok().body(
                statMetricService.find(
                        UUID.fromString(serverRequest.pathVariable("player")),
                        UUID.fromString(serverRequest.pathVariable("match"))
                ),
                StatMetric.class
        );
    }

    public Mono<ServerResponse> getPlayerStats(ServerRequest serverRequest){

        return ServerResponse.ok().body(
                statMetricService.getPlayerStats(
                        UUID.fromString(serverRequest.pathVariable("player"))
                ),
                StatMetric.class
        );
    }
}
