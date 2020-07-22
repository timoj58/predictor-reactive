package com.timmytime.predictorplayersreactive.service.impl;

import com.timmytime.predictorplayersreactive.facade.WebClientFacade;
import com.timmytime.predictorplayersreactive.service.TensorflowTrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("tensorflowTrainingService")
public class TensorflowTrainingServiceImpl implements TensorflowTrainingService {

    private final String trainingHost;
    private final WebClientFacade webClientFacade;

    @Autowired
    public TensorflowTrainingServiceImpl(
            @Value("${training.host}") String trainingHost,
            WebClientFacade webClientFacade
    ){
        this.trainingHost = trainingHost;
        this.webClientFacade = webClientFacade;
    }

    @Override
    public void train(UUID id) {
        //TODO.

    }
}
