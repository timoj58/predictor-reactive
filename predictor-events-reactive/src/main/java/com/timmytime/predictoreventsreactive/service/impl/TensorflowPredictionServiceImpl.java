package com.timmytime.predictoreventsreactive.service.impl;

import com.timmytime.predictoreventsreactive.enumerator.Predictions;
import com.timmytime.predictoreventsreactive.facade.WebClientFacade;
import com.timmytime.predictoreventsreactive.request.TensorflowPrediction;
import com.timmytime.predictoreventsreactive.service.TensorflowPredictionService;
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
    private final String resultsUrl;
    private final String goalsUrl;
    private final WebClientFacade webClientFacade;


    @Autowired
    public TensorflowPredictionServiceImpl(
            @Value("${clients.training}") String trainingHost,
            @Value("${clients.ml-predict-result}") String resultsUrl,
            @Value("${clients.ml-predict-goals}") String goalsUrl,
            WebClientFacade webClientFacade
    ) {
        this.trainingHost = trainingHost;
        this.resultsUrl = resultsUrl;
        this.goalsUrl = goalsUrl;
        this.webClientFacade = webClientFacade;
    }


    @Override
    public void predict(TensorflowPrediction tensorflowPrediction) {
        log.info("predicting id {} {} {}",
                tensorflowPrediction.getPrediction().getId(),
                tensorflowPrediction.getPrediction().getHome(),
                tensorflowPrediction.getPrediction().getAway());

        webClientFacade.predict(
                trainingHost + getUrl(tensorflowPrediction.getPredictions())
                        .replace("<receipt>", tensorflowPrediction.getPrediction().getId().toString())
                        .replace("<country>", tensorflowPrediction.getCountry()),
                tensorflowPrediction.getPrediction()
        );
    }

    private String getUrl(Predictions predictions) {
        switch (predictions) {
            case PREDICT_GOALS:
                return goalsUrl;
            case PREDICT_RESULTS:
                return resultsUrl;
        }

        return "";
    }
}
