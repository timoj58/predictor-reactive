package com.timmytime.predictorclientreactive.service.impl;

import com.timmytime.predictorclientreactive.enumerator.LambdaFunctions;
import com.timmytime.predictorclientreactive.facade.LambdaFacade;
import com.timmytime.predictorclientreactive.facade.S3Facade;
import com.timmytime.predictorclientreactive.facade.WebClientFacade;
import com.timmytime.predictorclientreactive.service.StartupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.stream.Stream;

@Service("startupService")
public class StartupServiceImpl implements StartupService {

    private final LambdaFacade lambdaFacade;
    private final WebClientFacade webClientFacade;
    private final S3Facade s3Facade;

    private final String dataScraperHost;
    private final String eventScraperHost;
    private final Integer startDelay;

    @Autowired
    public StartupServiceImpl(
            @Value("${delays.start}") Integer startDelay,
            @Value("${clients.data-scraper}") String dataScraperHost,
            @Value("${clients.event-scraper}") String eventScraperHost,
            LambdaFacade lambdaFacade,
            WebClientFacade webClientFacade,
            S3Facade s3Facade
    ) {
        this.startDelay = startDelay;
        this.dataScraperHost = dataScraperHost;
        this.eventScraperHost = eventScraperHost;
        this.lambdaFacade = lambdaFacade;
        this.webClientFacade = webClientFacade;
        this.s3Facade = s3Facade;
    }


    @Override
    // @PostConstruct
    public void start() throws InterruptedException {

        Flux.fromStream(
                Stream.of("fixtures",
                        "player-events",
                        "previous-events",
                        "previous-fixtures",
                        "top-performers",
                        "upcoming-events")
        ).doOnNext(directory -> s3Facade.archive(directory))
                .doFinally(start ->

                        Flux.fromStream(
                                Stream.of(
                                        LambdaFunctions.DATABASE,
                                        LambdaFunctions.PRE_START,
                                        LambdaFunctions.START
                                )
                        ).limitRate(1)
                                .delayElements(Duration.ofMinutes(startDelay))
                                .doOnNext(function ->
                                        lambdaFacade.invoke(function.getFunctionName())
                                )
                                .doFinally(wakeup ->
                                        Mono.just("/scrape")
                                                .delayElement(Duration.ofMinutes(startDelay))
                                                .subscribe(url -> {
                                                    webClientFacade.startScraper(dataScraperHost + url);
                                                    webClientFacade.startScraper(eventScraperHost + url);
                                                }))
                                .subscribe()
                )
                .subscribe();

    }

}
