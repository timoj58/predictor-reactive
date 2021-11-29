package com.timmytime.predictorplayersreactive.service.impl;

import com.timmytime.predictorplayersreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayersreactive.facade.WebClientFacade;
import com.timmytime.predictorplayersreactive.service.PlayersTrainingHistoryService;
import com.timmytime.predictorplayersreactive.service.TensorflowTrainingService;
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
    private final String yellowUrl;
    private final PlayersTrainingHistoryService playersTrainingHistoryService;
    private final WebClientFacade webClientFacade;

    @Autowired
    public TensorflowTrainingServiceImpl(
            @Value("${clients.training}") String trainingHost,
            @Value("${clients.ml-train-goals}") String goalsUrl,
            @Value("${clients.ml-train-assists}") String assistsUrl,
            @Value("${clients.ml-train-yellow}") String yellowUrl,
            PlayersTrainingHistoryService playersTrainingHistoryService,
            WebClientFacade webClientFacade
    ) {
        this.trainingHost = trainingHost;
        this.goalsUrl = goalsUrl;
        this.assistsUrl = assistsUrl;
        this.yellowUrl = yellowUrl;
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
            case YELLOW_CARD:
                return yellowUrl;
        }

        return "";
    }
}
