package com.timmytime.predictorteamsreactive.service.impl;

import com.timmytime.predictorteamsreactive.enumerator.CountryCompetitions;
import com.timmytime.predictorteamsreactive.enumerator.Training;
import com.timmytime.predictorteamsreactive.facade.WebClientFacade;
import com.timmytime.predictorteamsreactive.model.CountryMatch;
import com.timmytime.predictorteamsreactive.model.TrainingHistory;
import com.timmytime.predictorteamsreactive.service.TensorflowDataService;
import com.timmytime.predictorteamsreactive.service.TensorflowTrainService;
import com.timmytime.predictorteamsreactive.service.TrainingHistoryService;
import com.timmytime.predictorteamsreactive.service.TrainingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Slf4j
@Service("trainingService")
public class TrainingServiceImpl implements TrainingService {

    private final TensorflowDataService tensorflowDataService;
    private final TensorflowTrainService tensorflowTrainService;
    private final TrainingHistoryService trainingHistoryService;
    private final WebClientFacade webClientFacade;
    private final Integer interval;
    private final String dataHost;
    private final Integer trainingDelay;

    private Boolean evaluateMode = Boolean.FALSE;

    @Autowired
    public TrainingServiceImpl(
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
    public void train() {

        log.info("training init");

        evaluateMode = Boolean.TRUE;

        Flux.fromStream(
                Arrays.asList(CountryCompetitions.values()).stream()
        ).delayElements(Duration.ofMinutes(trainingDelay))
                .subscribe(country -> {

                    TrainingHistory trainingHistory = init(Training.TRAIN_RESULTS, country.name());
                    webClientFacade.getMatches(
                            dataHost + "/match/country/" + trainingHistory.getCountry()
                                    + "/" + trainingHistory.getFromDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                                    + "/" + trainingHistory.getToDate().plusYears(interval * 2).toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                    ).doOnNext(match -> tensorflowDataService.load(new CountryMatch(trainingHistory.getCountry(), match)))
                            .doFinally(f -> tensorflowTrainService.train(trainingHistory))
                            .subscribe();
                });

    }

    /*
      TODO need to seperate evaluation training, and normal training
     */

    @Override
    public Boolean train(TrainingHistory trainingHistory, Boolean init) {
        //NEED to tidy all of this up given various states (goals/results and evaluation mode etc)
        if (init || trainingHistory.getToDate().isBefore(LocalDate.now().atStartOfDay())) {

            TrainingHistory next = init ? trainingHistory :
                    trainingHistoryService.save(
                            new TrainingHistory(
                                    trainingHistory.getType(),
                                    trainingHistory.getCountry(),
                                    trainingHistory.getToDate(),
                                    trainingHistory.getToDate().plusYears(interval).isAfter(LocalDateTime.now()) ?
                                            LocalDateTime.now() : trainingHistory.getToDate().plusYears(interval)
                            )
                    );

            if (trainingHistory.getType().equals(Training.TRAIN_RESULTS)) {
                //and also need to load our next section....
                webClientFacade.getMatches(
                        dataHost + "/match/country/" + trainingHistory.getCountry()
                                //note: minus 1 months due to getting out of synch with goals.  can remove at some point.
                                + "/" + next.getFromDate().toLocalDate().minusMonths(1).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                                + "/" +
                                (evaluateMode ?
                                        next.getToDate().toLocalDate().plusYears(interval).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                                        :
                                        next.getToDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                                )
                ).doOnNext(match -> tensorflowDataService.load(new CountryMatch(next.getCountry(), match)))
                        .doFinally(f -> tensorflowTrainService.train(next))
                        .subscribe();
            } else {
                Mono.just(next)
                        .subscribe(history -> tensorflowTrainService.train(history));
            }

        } else {
            log.info("we have completed {} - {}", trainingHistory.getCountry(), trainingHistory.getType());
            trainingHistory.setCompleted(Boolean.TRUE);
            trainingHistoryService.save(trainingHistory);

            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    @Override
    public TrainingHistory init(Training type, String country) {
        TrainingHistory previous = trainingHistoryService.find(type, country.toLowerCase());
        return trainingHistoryService.save(
                new TrainingHistory(
                        type,
                        country.toLowerCase(),
                        previous.getToDate(),
                        previous.getToDate().plusYears(interval).isAfter(LocalDateTime.now()) ?
                                LocalDateTime.now() :
                                previous.getToDate().plusYears(interval)
                )
        );
    }
}
