package com.timmytime.predictorplayersreactive.service.impl;

import com.timmytime.predictorplayersreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayersreactive.facade.WebClientFacade;
import com.timmytime.predictorplayersreactive.service.PlayersTrainingHistoryService;
import com.timmytime.predictorplayersreactive.service.TensorflowTrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service("tensorflowTrainingService")
public class TensorflowTrainingServiceImpl implements TensorflowTrainingService {

    private final Logger log = LoggerFactory.getLogger(TensorflowTrainingServiceImpl.class);

    private final String trainingHost;
    private final String goalsUrl;
    private final String assistsUrl;
    private final String minutesUrl;
    private final String concededUrl;
    private final String savesUrl;
    private final String redUrl;
    private final String yellowUrl;
    private final PlayersTrainingHistoryService playersTrainingHistoryService;
    private final WebClientFacade webClientFacade;

    @Autowired
    public TensorflowTrainingServiceImpl(
            @Value("${training.host}") String trainingHost,
            @Value("${ml.train.goals.url}") String goalsUrl,
            @Value("${ml.train.assists.url}") String assistsUrl,
            @Value("${ml.train.minutes.url}") String minutesUrl,
            @Value("${ml.train.conceded.url}") String concededUrl,
            @Value("${ml.train.saves.url}") String savesUrl,
            @Value("${ml.train.red.url}") String redUrl,
            @Value("${ml.train.yellow.url}") String yellowUrl,
            PlayersTrainingHistoryService playersTrainingHistoryService,
            WebClientFacade webClientFacade
    ) {
        this.trainingHost = trainingHost;
        this.goalsUrl = goalsUrl;
        this.assistsUrl = assistsUrl;
        this.concededUrl = concededUrl;
        this.minutesUrl = minutesUrl;
        this.yellowUrl = yellowUrl;
        this.redUrl = redUrl;
        this.savesUrl = savesUrl;
        this.playersTrainingHistoryService = playersTrainingHistoryService;
        this.webClientFacade = webClientFacade;
    }

    @Override
    public void train(UUID id) {
        log.info("training started {}", id);

        playersTrainingHistoryService.find(id)
                .subscribe(history ->
                        webClientFacade.train(
                                trainingHost
                                        + getUrl(history.getType())
                                        .replace("<from>", history.getFromDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                                        .replace("<to>", history.getToDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                                        .replace("<receipt>", id.toString())
                        )
                );

    }

    private String getUrl(FantasyEventTypes fantasyEventTypes) {
        switch (fantasyEventTypes) {
            case GOALS:
                return goalsUrl;
            case ASSISTS:
                return assistsUrl;
            case SAVES:
                return savesUrl;
            case MINUTES:
                return minutesUrl;
            case GOALS_CONCEDED:
                return concededUrl;
            case RED_CARD:
                return redUrl;
            case YELLOW_CARD:
                return yellowUrl;
        }

        return "";
    }
}
