package com.timmytime.predictorclientreactive.service.impl;

import com.timmytime.predictorclientreactive.enumerator.LambdaFunctions;
import com.timmytime.predictorclientreactive.facade.LambdaFacade;
import com.timmytime.predictorclientreactive.facade.WebClientFacade;
import com.timmytime.predictorclientreactive.service.StartupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;

@Service("startupService")
public class StartupServiceImpl implements StartupService {

    private final LambdaFacade lambdaFacade;
    private final WebClientFacade webClientFacade;

    private final String dataScraperHost;
    private final String eventScraperHost;

    @Autowired
    public StartupServiceImpl(
            @Value("${data.scraper.host}") String dataScraperHost,
            @Value("${event.scraper.host}") String eventScraperHost,
            LambdaFacade lambdaFacade,
            WebClientFacade webClientFacade
    ){
        this.dataScraperHost = dataScraperHost;
        this.eventScraperHost = eventScraperHost;
        this.lambdaFacade = lambdaFacade;
        this.webClientFacade = webClientFacade;
    }

    //TODO

    @Override
   // @PostConstruct
    public void start() throws InterruptedException {
        lambdaFacade.invoke(LambdaFunctions.INIT.getFunctionName());
        Thread.sleep(Duration.ofMinutes(3).toMillis());
        lambdaFacade.invoke(LambdaFunctions.PRE_START.getFunctionName());
        Thread.sleep(Duration.ofMinutes(3).toMillis());
        lambdaFacade.invoke(LambdaFunctions.START.getFunctionName());
        Thread.sleep(Duration.ofMinutes(3).toMillis());
        //finally.
        webClientFacade.startScraper(dataScraperHost+"/scrape");
        webClientFacade.startScraper(eventScraperHost+"/scrape");
    }
}
