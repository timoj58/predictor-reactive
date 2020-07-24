package com.timmytime.predictorteamsreactive.service.impl;

import com.timmytime.predictorteamsreactive.enumerator.Training;
import com.timmytime.predictorteamsreactive.facade.WebClientFacade;
import com.timmytime.predictorteamsreactive.model.TrainingHistory;
import com.timmytime.predictorteamsreactive.service.TensorflowTrainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;


@Service("tensorflowTrainService")
public class TensorflowTrainServiceImpl implements TensorflowTrainService {

    private static final Logger log = LoggerFactory.getLogger(TensorflowTrainServiceImpl.class);

    private final WebClientFacade webClientFacade;
    private final String trainingHost;
    private final String resultsUrl;
    private final String goalsUrl;

    private final Integer trainingDelay;


    @Autowired
    public TensorflowTrainServiceImpl(
            @Value("${training.host}") String trainingHost,
            @Value("${ml.train.result.url}") String resultsUrl,
            @Value("${ml.train.goals.url}") String goalsUrl,
             @Value("${training.delay}") Integer trainingDelay,
             WebClientFacade webClientFacade
    ){
        this.trainingHost = trainingHost;
        this.resultsUrl = resultsUrl;
        this.goalsUrl = goalsUrl;
        this.trainingDelay = trainingDelay;
        this.webClientFacade = webClientFacade;
    }

    @Override
    public void train(TrainingHistory trainingHistory) {
        log.info("training {}", trainingHistory.getCountry());

        Flux.fromStream(
                Arrays.asList(Training.values()).stream()
        ).delayElements(Duration.ofSeconds(trainingDelay))
                .subscribe(type ->
                        webClientFacade.train(
                                trainingHost
                                        +getUrl(type)
                                        .replace("<receipt>", trainingHistory.getId().toString())
                                        .replace("<country>", trainingHistory.getCountry()))
                );


    }

    private String getUrl(Training training) {
        switch (training) {
            case TRAIN_GOALS:
                return goalsUrl;
            case TRAIN_RESULTS:
                return resultsUrl;
        }

        return "";
    }
}
