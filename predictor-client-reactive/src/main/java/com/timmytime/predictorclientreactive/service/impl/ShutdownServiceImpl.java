package com.timmytime.predictorclientreactive.service.impl;

import com.timmytime.predictorclientreactive.enumerator.LambdaFunctions;
import com.timmytime.predictorclientreactive.facade.LambdaFacade;
import com.timmytime.predictorclientreactive.service.ShutdownService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service("shutdownService")
public class ShutdownServiceImpl implements ShutdownService {

    private final LambdaFacade lambdaFacade;
    private final List<String> received = new ArrayList<>();

    @Autowired
    public ShutdownServiceImpl(
            LambdaFacade lambdaFacade
    ){
        this.lambdaFacade = lambdaFacade;
    }

    @Override
    public void receive(String service) {
        received.add(service);
        if(received.containsAll(Arrays.asList(
                BetServiceImpl.class.getName(),
                CompetitionServiceImpl.class.getName(),
                PlayersMatchServiceImpl.class.getName(),
                FixtureServiceImpl.class.getName(),
                PreviousFixtureServiceImpl.class.getName()
        ))){
            shutdown();
        }
    }

    @Override
    public void shutdown() {
        lambdaFacade.invoke(LambdaFunctions.PROXY_STOP.getFunctionName());

        Mono.just("exit").delayElement(Duration.ofMinutes(5))
                .subscribe(s -> lambdaFacade.invoke(LambdaFunctions.SHUTDOWN.getFunctionName())
                );
    }
}
