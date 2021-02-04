package com.timmytime.predictorteamsreactive.service.impl;

import com.timmytime.predictorteamsreactive.enumerator.CountryCompetitions;
import com.timmytime.predictorteamsreactive.enumerator.Training;
import com.timmytime.predictorteamsreactive.facade.WebClientFacade;
import com.timmytime.predictorteamsreactive.model.CountryMatch;
import com.timmytime.predictorteamsreactive.model.TrainingHistory;
import com.timmytime.predictorteamsreactive.service.TensorflowDataService;
import com.timmytime.predictorteamsreactive.service.TensorflowTrainService;
import com.timmytime.predictorteamsreactive.service.TrainingHistoryService;
import com.timmytime.predictorteamsreactive.service.TrainingModelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Slf4j
@Service("trainingModelService")
public class TrainingModelServiceImpl implements TrainingModelService {

    private final TensorflowDataService tensorflowDataService;
    private final TensorflowTrainService tensorflowTrainService;
    private final TrainingHistoryService trainingHistoryService;
    private final WebClientFacade webClientFacade;
    private final Integer interval;
    private final Integer trainingDelay;
    private final String dataHost;


    @Autowired
    public TrainingModelServiceImpl(
            @Value("${clients.data}") String dataHost,
            @Value("${delays.interval}") Integer interval,
            @Value("${delays.training-init}") Integer trainingDelay,
            TensorflowDataService tensorflowDataService,
            TensorflowTrainService tensorflowTrainService,
            TrainingHistoryService trainingHistoryService,
            WebClientFacade webClientFacade
    ) {
        this.dataHost = dataHost;
        this.interval = interval;
        this.trainingDelay = trainingDelay;
        this.tensorflowDataService = tensorflowDataService;
        this.tensorflowTrainService = tensorflowTrainService;
        this.trainingHistoryService = trainingHistoryService;
        this.webClientFacade = webClientFacade;
    }


    @Override
    public void create() {

        log.info("training init");

        Flux.fromStream(
                Arrays.stream(CountryCompetitions.values())
        ).delayElements(Duration.ofMinutes(trainingDelay))
                .subscribe(country -> {

                    TrainingHistory trainingHistory = trainingHistoryService.next(Training.TRAIN_RESULTS, country.name(), interval);
                    webClientFacade.getMatches(
                            dataHost + "/match/country/" + trainingHistory.getCountry()
                                    + "/" + trainingHistory.getFromDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                                    + "/" + trainingHistory.getToDate().plusYears(interval).toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                    ).doOnNext(match -> tensorflowDataService.load(new CountryMatch(trainingHistory.getCountry(), match)))
                            .doFinally(f -> tensorflowTrainService.train(trainingHistory))
                            .subscribe();
                });

    }

}
