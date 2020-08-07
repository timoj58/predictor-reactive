package com.timmytime.predictorclientreactive.service.impl;

import com.timmytime.predictorclientreactive.facade.LambdaFacade;
import com.timmytime.predictorclientreactive.service.StartupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service("startupService")
public class StartupServiceImpl implements StartupService {

    private final LambdaFacade lambdaFacade;

    @Autowired
    public StartupServiceImpl(
            LambdaFacade lambdaFacade
    ){
        this.lambdaFacade = lambdaFacade;
    }

    /*
      use this service to start all other services, given it also stops all other services.

      note: only config server starts before this.

      + no database needed etc.
     */

    @Override
    @PostConstruct
    public void start() {
        /*
          so...

          most likely

          1: start up database + proxy
          2: startup scrapers
          3: start up data service
          4: start up data events, teams, players and events services
          5: start up machine learning services
          6: tell scrapers to begin.....

         */

    }
}
