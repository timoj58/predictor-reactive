package com.timmytime.predictorteamsreactive.service.impl;

import com.timmytime.predictorteamsreactive.enumerator.Training;
import com.timmytime.predictorteamsreactive.facade.WebClientFacade;
import com.timmytime.predictorteamsreactive.model.CountryMatch;
import com.timmytime.predictorteamsreactive.model.TrainingHistory;
import com.timmytime.predictorteamsreactive.service.TensorflowDataService;
import com.timmytime.predictorteamsreactive.service.TensorflowTrainService;
import com.timmytime.predictorteamsreactive.service.TrainingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

@Slf4j
@Service("trainingService")
public class TrainingServiceImpl implements TrainingService {

    private final TensorflowDataService tensorflowDataService;
    private final TensorflowTrainService tensorflowTrainService;
    private final WebClientFacade webClientFacade;
    private final Integer interval;
    private final String dataHost;
    private final Boolean trainingEvaluation;

    @Autowired
    public TrainingServiceImpl(
            @Value("${clients.data}") String dataHost,
            @Value("${delays.interval}") Integer interval,
            @Value("${training.evaluation}") Boolean trainingEvaluation,
            TensorflowDataService tensorflowDataService,
            TensorflowTrainService tensorflowTrainService,
            WebClientFacade webClientFacade
    ) {
        this.dataHost = dataHost;
        this.interval = interval;
        this.trainingEvaluation = trainingEvaluation;
        this.tensorflowDataService = tensorflowDataService;
        this.tensorflowTrainService = tensorflowTrainService;
        this.webClientFacade = webClientFacade;
    }


    @Override
    public void train(Function<Integer, TrainingHistory> trainingHistoryFunction) {
        var trainingHistory = trainingHistoryFunction.apply(interval);

        if (trainingHistory.getType().equals(Training.TRAIN_RESULTS)) {
            //and also need to load our next section....
            log.info("loading matches for {}", trainingHistory.getCountry());
            webClientFacade.getMatches(
                            dataHost + "/match/country/" + trainingHistory.getCountry()
                                    + "/" + trainingHistory.getFromDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                                    + "/" +
                                    (trainingEvaluation ?
                                            trainingHistory.getToDate().toLocalDate().plusYears(interval).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                                            :
                                            trainingHistory.getToDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                                    )
                    ).doOnNext(match -> tensorflowDataService.load(new CountryMatch(trainingHistory.getCountry(), match)))
                    .doFinally(f -> Mono.just(trainingHistory)
                            .delayElement(Duration.ofSeconds(interval))
                            .subscribe(tensorflowTrainService::train))
                    .subscribe();
        } else {
            Mono.just(trainingHistory)
                    .subscribe(tensorflowTrainService::train);
        }

    }


}
