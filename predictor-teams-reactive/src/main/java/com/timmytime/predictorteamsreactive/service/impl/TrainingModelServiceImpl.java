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
import reactor.core.publisher.Mono;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service("trainingModelService")
public class TrainingModelServiceImpl implements TrainingModelService {

    private final TensorflowDataService tensorflowDataService;
    private final TensorflowTrainService tensorflowTrainService;
    private final TrainingHistoryService trainingHistoryService;
    private final WebClientFacade webClientFacade;
    private final Integer interval;
    private final String dataHost;

    private final List<CountryCompetitions> countriesProcessed = new ArrayList<>();


    @Autowired
    public TrainingModelServiceImpl(
            @Value("${clients.data}") String dataHost,
            @Value("${delays.interval}") Integer interval,
            TensorflowDataService tensorflowDataService,
            TensorflowTrainService tensorflowTrainService,
            TrainingHistoryService trainingHistoryService,
            WebClientFacade webClientFacade
    ) {
        this.dataHost = dataHost;
        this.interval = interval;
        this.tensorflowDataService = tensorflowDataService;
        this.tensorflowTrainService = tensorflowTrainService;
        this.trainingHistoryService = trainingHistoryService;
        this.webClientFacade = webClientFacade;

        countriesProcessed.addAll(Arrays.asList(CountryCompetitions.values()));
    }


    @Override
    public void create() {
        if (!countriesProcessed.isEmpty()) {
            var toProcess = countriesProcessed.remove(0);
            log.info("training init {}", toProcess.name());
            Mono.just(toProcess)
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
        } else {
            log.info("all countries have been trained");
        }

    }

}
