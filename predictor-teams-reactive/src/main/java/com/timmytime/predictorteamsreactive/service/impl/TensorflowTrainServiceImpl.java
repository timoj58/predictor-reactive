package com.timmytime.predictorteamsreactive.service.impl;

import com.timmytime.predictorteamsreactive.enumerator.Training;
import com.timmytime.predictorteamsreactive.facade.WebClientFacade;
import com.timmytime.predictorteamsreactive.model.TrainingHistory;
import com.timmytime.predictorteamsreactive.service.TensorflowTrainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Slf4j
@Service("tensorflowTrainService")
public class TensorflowTrainServiceImpl implements TensorflowTrainService {

    private final WebClientFacade webClientFacade;
    private final String trainingHost;
    private final String resultsUrl;
    private final String goalsUrl;


    @Autowired
    public TensorflowTrainServiceImpl(
            @Value("${clients.training}") String trainingHost,
            @Value("${clients.ml-train-result}") String resultsUrl,
            @Value("${clients.ml-train-goals}") String goalsUrl,
            WebClientFacade webClientFacade
    ) {
        this.trainingHost = trainingHost;
        this.resultsUrl = resultsUrl;
        this.goalsUrl = goalsUrl;
        this.webClientFacade = webClientFacade;
    }

    @Override
    public void train(TrainingHistory trainingHistory) {
        log.info("training {} {}", trainingHistory.getCountry(), trainingHistory.getId());

        webClientFacade.train(
                trainingHost
                        + getUrl(trainingHistory.getType())
                        .replace("<receipt>", trainingHistory.getId().toString())
                        .replace("<country>", trainingHistory.getCountry())
                        .replace("<from>", trainingHistory.getFromDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                        .replace("<to>", trainingHistory.getToDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
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
