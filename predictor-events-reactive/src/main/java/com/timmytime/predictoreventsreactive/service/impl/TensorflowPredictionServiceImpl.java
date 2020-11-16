package com.timmytime.predictoreventsreactive.service.impl;

import com.timmytime.predictoreventsreactive.enumerator.Predictions;
import com.timmytime.predictoreventsreactive.facade.WebClientFacade;
import com.timmytime.predictoreventsreactive.request.Prediction;
import com.timmytime.predictoreventsreactive.request.TensorflowPrediction;
import com.timmytime.predictoreventsreactive.service.TensorflowPredictionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.Consumer;

@Service("tensorflowPredictionService")
public class TensorflowPredictionServiceImpl implements TensorflowPredictionService {

    private static final Logger log = LoggerFactory.getLogger(TensorflowPredictionServiceImpl.class);

    private final String trainingHost;
    private final String resultsUrl;
    private final String goalsUrl;

    private final Integer delay;

    private final Flux<TensorflowPrediction> receiver;
    private Consumer<TensorflowPrediction> consumer;
    private Consumer<UUID> replay;

    private final WebClientFacade webClientFacade;

    @Autowired
    public TensorflowPredictionServiceImpl(
            @Value("${training.host}") String trainingHost,
            @Value("${ml.predict.result.url}") String resultsUrl,
            @Value("${ml.predict.goals.url}") String goalsUrl,
            @Value("${competition.delay}") Integer delay,
            WebClientFacade webClientFacade
    ){
        this.trainingHost = trainingHost;
        this.resultsUrl = resultsUrl;
        this.goalsUrl = goalsUrl;
        this.delay = delay;
        this.webClientFacade = webClientFacade;

        this.receiver
                = Flux.push(sink -> consumer = (t) -> sink.next(t), FluxSink.OverflowStrategy.BUFFER);
        this.receiver.delayElements(Duration.ofSeconds(delay*2))
                .limitRate(1)
                .doOnNext(this::process)
        .doFinally(end ->
            Mono.just(UUID.randomUUID())
                    .delayElement(Duration.ofMinutes(delay))
                    .subscribe(replay)
        ).subscribe();
    }


    @Override
    public void predict(TensorflowPrediction tensorflowPrediction) {
        consumer.accept(tensorflowPrediction);
    }

    @Override
    public void setReplayConsumer(Consumer<UUID> replay) {
         this.replay = replay;
    }


    private void process(TensorflowPrediction tensorflowPrediction){
        log.info("predicting id {} {} {}", tensorflowPrediction.getPrediction().getId(), tensorflowPrediction.getPrediction().getHome(), tensorflowPrediction.getPrediction().getAway());

        webClientFacade.predict(
                trainingHost + getUrl(tensorflowPrediction.getPredictions())
                        .replace("<receipt>", tensorflowPrediction.getPrediction().getId().toString())
                        .replace("<country>", tensorflowPrediction.getCountry()),
                tensorflowPrediction.getPrediction()
        );
    }

    private String getUrl(Predictions predictions) {
        switch (predictions) {
            case PREDICT_GOALS:
                return goalsUrl;
            case PREDICT_RESULTS:
                return resultsUrl;
        }

        return "";
    }
}
