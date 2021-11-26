package com.timmytime.predictorclientreactive.service.impl;

import com.timmytime.predictorclientreactive.enumerator.LambdaFunctions;
import com.timmytime.predictorclientreactive.facade.LambdaFacade;
import com.timmytime.predictorclientreactive.facade.S3Facade;
import com.timmytime.predictorclientreactive.facade.WebClientFacade;
import com.timmytime.predictorclientreactive.service.StartupService;
import com.timmytime.predictorclientreactive.service.TeamService;
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
    private final TeamService teamService;

    private final String dataScraperHost;
    private final String eventScraperHost;
    private final Integer startDelay;
    private final Boolean orchestrationEnabled;

    @Autowired
    public StartupServiceImpl(
            @Value("${orchestration.enabled}") Boolean orchestrationEnabled,
            @Value("${delays.start}") Integer startDelay,
            @Value("${clients.data-scraper}") String dataScraperHost,
            @Value("${clients.event-scraper}") String eventScraperHost,
            LambdaFacade lambdaFacade,
            WebClientFacade webClientFacade,
            S3Facade s3Facade,
            TeamService teamService
    ) {
        this.orchestrationEnabled = orchestrationEnabled;
        this.startDelay = startDelay;
        this.dataScraperHost = dataScraperHost;
        this.eventScraperHost = eventScraperHost;
        this.lambdaFacade = lambdaFacade;
        this.webClientFacade = webClientFacade;
        this.s3Facade = s3Facade;
        this.teamService = teamService;
    }


    @PostConstruct
    private void start() {
        if (orchestrationEnabled)
            conduct();
        else
            runAsync(teamService::loadTeams);
    }

    private void completeStartup(String url) {//TODO this will just call message now
        log.info("starting scrapers and loading teams");
        runAsync(() -> webClientFacade.startScraper(dataScraperHost + url))
                .thenRun(() -> webClientFacade.startScraper(eventScraperHost + url))
                .thenRun(teamService::loadTeams);
    }

    @Override
    public Mono<Void> conduct(){
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
                                                just("/scrape")
                                                        .delayElement(ofMinutes(startDelay))
                                                        .subscribe(this::completeStartup))
                                        .subscribe()
                        )
                        .subscribe()
        );

        return Mono.empty();
    }


}
