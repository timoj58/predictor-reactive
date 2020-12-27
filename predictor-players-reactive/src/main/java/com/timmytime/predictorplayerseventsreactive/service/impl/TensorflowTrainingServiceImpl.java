package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayerseventsreactive.facade.WebClientFacade;
import com.timmytime.predictorplayerseventsreactive.service.PlayersTrainingHistoryService;
import com.timmytime.predictorplayerseventsreactive.service.TensorflowTrainingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service("tensorflowTrainingService")
public class TensorflowTrainingServiceImpl implements TensorflowTrainingService {

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
            @Value("${clients.training}") String trainingHost,
            @Value("${clients.ml-train-goals}") String goalsUrl,
            @Value("${clients.ml-train-assists}") String assistsUrl,
            @Value("${clients.ml-train-minutes}") String minutesUrl,
            @Value("${clients.ml-train-conceded}") String concededUrl,
            @Value("${clients.ml-train-saves}") String savesUrl,
            @Value("${clients.ml-train-red}") String redUrl,
            @Value("${clients.ml-train-yellow}") String yellowUrl,
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
