package com.timmytime.predictorplayersreactive.service.impl;

import com.timmytime.predictorplayersreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayersreactive.facade.WebClientFacade;
import com.timmytime.predictorplayersreactive.request.PlayerEventOutcomeCsv;
import com.timmytime.predictorplayersreactive.request.TensorflowPrediction;
import com.timmytime.predictorplayersreactive.service.TensorflowPredictionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.time.Duration;
import java.util.function.Consumer;

@Service("tensorflowPredictionService")
public class TensorflowPredictionServiceImpl implements TensorflowPredictionService {

    private final Logger log = LoggerFactory.getLogger(TensorflowPredictionServiceImpl.class);

    private final String trainingHost;
    private final String goalsUrl;
    private final String assistsUrl;
    private final String minutesUrl;
    private final String concededUrl;
    private final String savesUrl;
    private final String redUrl;
    private final String yellowUrl;
    private final String initUrl;
    private final String destroyUrl;

    private final WebClientFacade webClientFacade;


    private final Flux<TensorflowPrediction> receiver;
    private Consumer<TensorflowPrediction> consumer;


    @Autowired
    public TensorflowPredictionServiceImpl(
            @Value("${training.host}") String trainingHost,
            @Value("${ml.predict.goals.url}") String goalsUrl,
            @Value("${ml.predict.assists.url}") String assistsUrl,
            @Value("${ml.predict.minutes.url}") String minutesUrl,
            @Value("${ml.predict.conceded.url}") String concededUrl,
            @Value("${ml.predict.saves.url}") String savesUrl,
            @Value("${ml.predict.red.url}") String redUrl,
            @Value("${ml.predict.yellow.url}") String yellowUrl,
            @Value("${ml.predict.init.url}") String initUrl,
            @Value("${ml.predict.destroy.url}") String destroyUrl,
            WebClientFacade webClientFacade
    ){
        this.trainingHost = trainingHost;
        this.goalsUrl = goalsUrl;
        this.assistsUrl = assistsUrl;
        this.concededUrl = concededUrl;
        this.minutesUrl = minutesUrl;
        this.yellowUrl = yellowUrl;
        this.redUrl = redUrl;
        this.savesUrl = savesUrl;
        this.initUrl = initUrl;
        this.destroyUrl = destroyUrl;

        this.webClientFacade = webClientFacade;

        this.receiver
                = Flux.push(sink -> consumer = (t) -> sink.next(t), FluxSink.OverflowStrategy.BUFFER);
        this.receiver.delayElements(Duration.ofMillis(350)).subscribe(this::process);
    }

    @Override
    public void predict(TensorflowPrediction tensorflowPrediction) {
        consumer.accept(tensorflowPrediction);
    }

    @Override
    public void init(String type) {
        webClientFacade.config(
                trainingHost
                        +initUrl.replace("<type>", type));
    }

    @Override
    public void destroy(String type) {
        webClientFacade.config(
                trainingHost
                        +destroyUrl.replace("<type>", type));

    }

    private void process(TensorflowPrediction tensorflowPrediction){
        log.info("predicting {} {}",  tensorflowPrediction.getPlayerEventOutcomeCsv().getPlayer(), tensorflowPrediction.getPlayerEventOutcomeCsv().getOpponent());

        webClientFacade.predict(
                trainingHost
                        +getUrl(tensorflowPrediction.getFantasyEventTypes())
                        .replace("<receipt>", tensorflowPrediction.getPlayerEventOutcomeCsv().getId().toString())
                        .replace("<init>", "false"),
                tensorflowPrediction.getPlayerEventOutcomeCsv()
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
