package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayerseventsreactive.facade.WebClientFacade;
import com.timmytime.predictorplayerseventsreactive.request.TensorflowPrediction;
import com.timmytime.predictorplayerseventsreactive.service.TensorflowPredictionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Slf4j
@Service("tensorflowPredictionService")
public class TensorflowPredictionServiceImpl implements TensorflowPredictionService {

    private final String trainingHost;
    private final String goalsUrl;
    private final String assistsUrl;
    private final String yellowUrl;
    private final String initUrl;
    private final String destroyUrl;

    private final WebClientFacade webClientFacade;


    @Autowired
    public TensorflowPredictionServiceImpl(
            @Value("${clients.training}") String trainingHost,
            @Value("${clients.ml-predict-goals}") String goalsUrl,
            @Value("${clients.ml-predict-assists}") String assistsUrl,
            @Value("${clients.ml-predict-yellow}") String yellowUrl,
            @Value("${clients.ml-predict-init}") String initUrl,
            @Value("${clients.ml-predict-destroy}") String destroyUrl,
            WebClientFacade webClientFacade
    ) {
        this.trainingHost = trainingHost;
        this.goalsUrl = goalsUrl;
        this.assistsUrl = assistsUrl;
        this.yellowUrl = yellowUrl;
        this.initUrl = initUrl;
        this.destroyUrl = destroyUrl;

        this.webClientFacade = webClientFacade;

    }

    @Override
    public void predict(TensorflowPrediction tensorflowPrediction) {

        webClientFacade.predict(
                trainingHost
                        + getUrl(tensorflowPrediction.getFantasyEventTypes())
                        .replace("<init>", "false"),
                tensorflowPrediction.getPlayerEventOutcomeCsv()
        );
    }

    @Override
    public void init(String type) {
        //TODO review
        webClientFacade.config(
                trainingHost
                        + initUrl.replace("<type>", type));
    }


    @Override
    public void destroy(String type) {
        webClientFacade.config(
                trainingHost
                        + destroyUrl.replace("<type>", type));

    }


    private String getUrl(FantasyEventTypes fantasyEventTypes) {
        switch (fantasyEventTypes) {
            case GOALS:
                return goalsUrl;
            case ASSISTS:
                return assistsUrl;
            case YELLOW_CARD:
                return yellowUrl;
        }

        return "";
    }
}
