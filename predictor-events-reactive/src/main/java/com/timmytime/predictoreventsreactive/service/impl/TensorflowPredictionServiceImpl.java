package com.timmytime.predictoreventsreactive.service.impl;

import com.timmytime.predictoreventsreactive.enumerator.Predictions;
import com.timmytime.predictoreventsreactive.facade.WebClientFacade;
import com.timmytime.predictoreventsreactive.request.Prediction;
import com.timmytime.predictoreventsreactive.service.TensorflowPredictionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;

@Service("tensorflowPredictionService")
public class TensorflowPredictionServiceImpl implements TensorflowPredictionService {

    private static final Logger log = LoggerFactory.getLogger(TensorflowPredictionServiceImpl.class);

    private final String trainingHost;
    private final String resultsUrl;
    private final String goalsUrl;


    private final WebClientFacade webClientFacade;

    @Autowired
    public TensorflowPredictionServiceImpl(
            @Value("${training.host}") String trainingHost,
            @Value("${ml.predict.result.url}") String resultsUrl,
            @Value("${ml.predict.goals.url}") String goalsUrl,
            WebClientFacade webClientFacade
    ){
        this.trainingHost = trainingHost;
        this.resultsUrl = resultsUrl;
        this.goalsUrl = goalsUrl;
        this.webClientFacade = webClientFacade;
    }


    @Override
    public void predict(Predictions predictions, Prediction prediction, String country) {
        log.info("predicting {}", prediction.getHome(), prediction.getAway());

        webClientFacade.predict(
                trainingHost+getUrl(predictions)
                        .replace("<receipt>", prediction.getId().toString())
                        .replace("<country>", country),
                prediction
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
