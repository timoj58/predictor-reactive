package com.timmytime.predictorclientreactive.service.impl;

import com.timmytime.predictorclientreactive.facade.S3Facade;
import com.timmytime.predictorclientreactive.facade.WebClientFacade;
import com.timmytime.predictorclientreactive.service.ILoadService;
import com.timmytime.predictorclientreactive.service.ShutdownService;
import com.timmytime.predictorclientreactive.util.Competition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Arrays;

@Service("playersMatchService")
public class PlayersMatchServiceImpl implements ILoadService {

    private final Logger log = LoggerFactory.getLogger(PlayersMatchServiceImpl.class);

    private final S3Facade s3Facade;
    private final WebClientFacade webClientFacade;
    private final ShutdownService shutdownService;


    @Autowired
    public PlayersMatchServiceImpl(
            S3Facade s3Facade,
            WebClientFacade webClientFacade,
            ShutdownService shutdownService
    ){
        this.s3Facade = s3Facade;
        this.webClientFacade = webClientFacade;
        this.shutdownService = shutdownService;
    }

    /*
      need to do top selections as well i suspect looking at the old code.
     */

    @Override
    public void load() {

        Flux.fromStream(
                Arrays.asList(
                        Competition.values()
                ).stream().filter(f -> f.getFantasyLeague() == Boolean.TRUE)
        ).subscribe(
                league -> {}
        );

    }
}
