package com.timmytime.predictorteamsreactive.service.impl;

import com.timmytime.predictorteamsreactive.enumerator.CountryCompetitions;
import com.timmytime.predictorteamsreactive.facade.WebClientFacade;
import com.timmytime.predictorteamsreactive.model.CountryMatch;
import com.timmytime.predictorteamsreactive.model.TrainingHistory;
import com.timmytime.predictorteamsreactive.service.TensorflowDataService;
import com.timmytime.predictorteamsreactive.service.TensorflowTrainService;
import com.timmytime.predictorteamsreactive.service.TrainingHistoryService;
import com.timmytime.predictorteamsreactive.service.TrainingService;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Service("trainingService")
public class TrainingServiceImpl implements TrainingService {

    private static final Logger log = LoggerFactory.getLogger(TrainingServiceImpl.class);

    private final TensorflowDataService tensorflowDataService;
    private final TensorflowTrainService tensorflowTrainService;
    private final TrainingHistoryService trainingHistoryService;
    private final WebClientFacade webClientFacade;
    private final Integer interval;
    private final String dataHost;
    private final Integer trainingDelay;

    @Autowired
    public TrainingServiceImpl(
            @Value("${data.host}") String dataHost,
            @Value("${interval}") Integer interval,
            @Value("${training.init.delay}") Integer trainingDelay,
            TensorflowDataService tensorflowDataService,
            TensorflowTrainService tensorflowTrainService,
            TrainingHistoryService trainingHistoryService,
            WebClientFacade webClientFacade
    ){
        this.dataHost = dataHost;
        this.interval = interval;
        this.trainingDelay = trainingDelay;
        this.tensorflowDataService = tensorflowDataService;
        this.tensorflowTrainService = tensorflowTrainService;
        this.trainingHistoryService = trainingHistoryService;
        this.webClientFacade = webClientFacade;
    }

    @Override
    public void train() {

        log.info("training init");

        Flux.fromStream(
                Arrays.asList(CountryCompetitions.values()).stream()
        ).delayElements(Duration.ofMinutes(trainingDelay))
                .subscribe(country -> {
                    TrainingHistory previous = trainingHistoryService.find(country.name());
                    TrainingHistory trainingHistory = trainingHistoryService.save(
                            new TrainingHistory(country.name(), previous.getToDate(), previous.getToDate().plusYears(interval))
                    );

                    webClientFacade.getMatches(
                            dataHost + "/match/country/" + trainingHistory.getCountry()
                                    + "/" + trainingHistory.getFromDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                                    + "/" + trainingHistory.getToDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                    ).doOnNext(match -> tensorflowDataService.load(new CountryMatch(trainingHistory.getCountry(), match)))
                            .doFinally(f -> tensorflowTrainService.train(trainingHistory))
                            .subscribe();
                });
    }

    @Override
    public void train(TrainingHistory trainingHistory) {

        tensorflowDataService.clear(trainingHistory.getCountry());

        trainingHistory.setCompleted(Boolean.TRUE);
        trainingHistoryService.save(trainingHistory);

        if(trainingHistory.getToDate().isBefore(LocalDate.now().atStartOfDay())) {

            TrainingHistory next = new TrainingHistory(trainingHistory.getCountry(), trainingHistory.getToDate(), trainingHistory.getToDate().plusYears(interval));

            webClientFacade.getMatches(
                    dataHost + "/match/country/" + next.getCountry()
                            + "/" + next.getFromDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                            + "/" + next.getToDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            ).doOnNext(match -> tensorflowDataService.load(new CountryMatch(next.getCountry(), match)))
                    .doFinally(f -> tensorflowTrainService.train(next))
                    .subscribe();
        }else{
            log.info("we have completed {}", trainingHistory.getCountry());
        }

    }
}
