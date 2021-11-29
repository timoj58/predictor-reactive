package com.timmytime.predictorclientreactive.service.impl;

import com.timmytime.predictorclientreactive.enumerator.LambdaFunctions;
import com.timmytime.predictorclientreactive.facade.LambdaFacade;
import com.timmytime.predictorclientreactive.facade.S3Facade;
import com.timmytime.predictorclientreactive.facade.WebClientFacade;
import com.timmytime.predictorclientreactive.request.Message;
import com.timmytime.predictorclientreactive.service.StartupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

import static java.time.Duration.ofMinutes;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.stream.Stream.of;
import static reactor.core.publisher.Flux.fromStream;
import static reactor.core.publisher.Mono.just;

@Slf4j
@Service("startupService")
public class StartupServiceImpl implements StartupService {

    private final LambdaFacade lambdaFacade;
    private final WebClientFacade webClientFacade;
    private final S3Facade s3Facade;

    private final String messageHost;
    private final Integer startDelay;
    private final Boolean orchestrationEnabled;

    @Autowired
    public StartupServiceImpl(
            @Value("${orchestration.enabled}") Boolean orchestrationEnabled,
            @Value("${delays.start}") Integer startDelay,
            @Value("${clients.message}") String messageHost,
            LambdaFacade lambdaFacade,
            WebClientFacade webClientFacade,
            S3Facade s3Facade
    ) {
        this.orchestrationEnabled = orchestrationEnabled;
        this.startDelay = startDelay;
        this.messageHost = messageHost;
        this.lambdaFacade = lambdaFacade;
        this.webClientFacade = webClientFacade;
        this.s3Facade = s3Facade;
    }


    @PostConstruct
    private void start() {
        if (orchestrationEnabled)
            conduct();
    }

    private void completeStartup(String url) {
        runAsync(() -> webClientFacade.sendMessage(url,
                Message.builder()
                        .event("START")
                        .eventType("ALL")
                        .build()));
    }

    @Override
    public Mono<Void> conduct() {
        runAsync(() ->

                fromStream(
                        of(
                                "fixtures",
                                "player-events",
                                "previous-events",
                                "previous-fixtures",
                                "top-performers",
                                "upcoming-events",
                                "selected-bets"
                        )
                )
                        .limitRate(1)
                        .doOnNext(s3Facade::archive)
                        .doFinally(start ->
                                Mono.just(LambdaFunctions.START)
                                        .delayElement(ofMinutes(startDelay))
                                        .map(LambdaFunctions::getFunctionName)
                                        .doOnNext(lambdaFacade::invoke)
                                        .doFinally(wakeup ->
                                                just(messageHost + "/message")
                                                        .delayElement(ofMinutes(startDelay))
                                                        .subscribe(this::completeStartup))
                                        .subscribe()
                        )
                        .subscribe()
        );

        return Mono.empty();
    }


}
