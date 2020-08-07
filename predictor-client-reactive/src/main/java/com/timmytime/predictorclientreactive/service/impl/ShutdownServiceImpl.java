package com.timmytime.predictorclientreactive.service.impl;

import com.timmytime.predictorclientreactive.facade.LambdaFacade;
import com.timmytime.predictorclientreactive.service.ShutdownService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("shutdownService")
public class ShutdownServiceImpl implements ShutdownService {

    private final LambdaFacade lambdaFacade;

    @Autowired
    public ShutdownServiceImpl(
            LambdaFacade lambdaFacade
    ){
        this.lambdaFacade = lambdaFacade;
    }


    /*
      this will turn off all microservices on completion.
     */

    @Override
    public void receive(String service) {

    }

    @Override
    public void shutdown() {

    }
}
