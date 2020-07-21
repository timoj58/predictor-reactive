package com.timmytime.predictorteamsreactive.service.impl;

import com.timmytime.predictorteamsreactive.facade.WebClientFacade;
import com.timmytime.predictorteamsreactive.model.*;
import com.timmytime.predictorteamsreactive.repo.TrainingHistoryRepo;
import com.timmytime.predictorteamsreactive.service.CompetitionService;
import com.timmytime.predictorteamsreactive.service.TensorflowDataService;
import com.timmytime.predictorteamsreactive.service.TensorflowTrainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service("competitionService")
public class CompetitionServiceImpl implements CompetitionService {

    private static final Logger log = LoggerFactory.getLogger(CompetitionServiceImpl.class);

    private final TensorflowTrainService tensorflowTrainService;
    private final TensorflowDataService tensorflowDataService;
    private final WebClientFacade webClientFacade;

    private final String dataHost;

    @Autowired
    public CompetitionServiceImpl(
            @Value("${data.host}") String dataHost,
            TensorflowTrainService tensorflowTrainService,
            TensorflowDataService tensorflowDataService,
            WebClientFacade webClientFacade
    ){
        this.dataHost = dataHost;
        this.tensorflowTrainService = tensorflowTrainService;
        this.tensorflowDataService = tensorflowDataService;
        this.webClientFacade = webClientFacade;
    }

    @Override
    public void load(TrainingHistory trainingHistory) {
        log.info("loading {}", trainingHistory.getCountry());

        webClientFacade.getMatches(
                dataHost+"/match/country/"+trainingHistory.getCountry()
                        +"/"+trainingHistory.getFromDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                        +"/"+trainingHistory.getToDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        ).doOnNext(match -> tensorflowDataService.load(new CountryMatch(trainingHistory.getCountry(), match)))
                .doFinally(f -> tensorflowTrainService.train(trainingHistory))
                .subscribe();

    }

}
