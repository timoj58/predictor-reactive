package com.timmytime.predictorclientreactive.service.impl;

import com.timmytime.predictorclientreactive.enumerator.LambdaFunctions;
import com.timmytime.predictorclientreactive.facade.LambdaFacade;
import com.timmytime.predictorclientreactive.facade.S3Facade;
import com.timmytime.predictorclientreactive.facade.WebClientFacade;
import com.timmytime.predictorclientreactive.service.StartupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service("startupService")
public class StartupServiceImpl implements StartupService {

    private final LambdaFacade lambdaFacade;
    private final WebClientFacade webClientFacade;
    private final S3Facade s3Facade;

    private final String dataScraperHost;
    private final String eventScraperHost;

    @Autowired
    public StartupServiceImpl(
            @Value("${clients.data-scraper}") String dataScraperHost,
            @Value("${clients.event-scraper}") String eventScraperHost,
            LambdaFacade lambdaFacade,
            WebClientFacade webClientFacade,
            S3Facade s3Facade
    ) {
        this.dataScraperHost = dataScraperHost;
        this.eventScraperHost = eventScraperHost;
        this.lambdaFacade = lambdaFacade;
        this.webClientFacade = webClientFacade;
        this.s3Facade = s3Facade;
    }


    @Override
    // @PostConstruct
    public void start() throws InterruptedException {

        //TODO review this in future. Mono.just(1).subscribe(s -> s3Facade.archive());

        lambdaFacade.invoke(LambdaFunctions.DATABASE.getFunctionName());
        Thread.sleep(Duration.ofMinutes(3).toMillis());
        lambdaFacade.invoke(LambdaFunctions.PRE_START.getFunctionName());
        Thread.sleep(Duration.ofMinutes(3).toMillis());
        lambdaFacade.invoke(LambdaFunctions.START.getFunctionName());
        Thread.sleep(Duration.ofMinutes(3).toMillis());

        webClientFacade.startScraper(dataScraperHost + "/scrape");
        webClientFacade.startScraper(eventScraperHost + "/scrape");
    }
}
