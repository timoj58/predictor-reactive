package com.timmytime.predictorclientreactive.service.impl;

import com.timmytime.predictorclientreactive.enumerator.LambdaFunctions;
import com.timmytime.predictorclientreactive.facade.LambdaFacade;
import com.timmytime.predictorclientreactive.model.BetProvider;
import com.timmytime.predictorclientreactive.service.ShutdownService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Service("shutdownService")
@Slf4j
public class ShutdownServiceImpl implements ShutdownService {

    private final LambdaFacade lambdaFacade;
    private final List<String> received = new ArrayList<>();

    @Autowired
    public ShutdownServiceImpl(
            LambdaFacade lambdaFacade
    ) {
        this.lambdaFacade = lambdaFacade;
    }

    @Override
    public void receive(String service) {
        log.info("receiving shutdown message for {}", service);
        CompletableFuture.runAsync(() -> received.add(service))
                .thenRun(() -> {
                    log.info("checking status?...");
                    if (received.containsAll(Arrays.asList(
                            CompetitionServiceImpl.class.getName(),
                            PlayersMatchServiceImpl.class.getName(),
                            FixtureServiceImpl.class.getName(),
                            PreviousFixtureServiceImpl.class.getName(),
                            TeamsMatchServiceImpl.class.getName(),
                            PreviousOutcomesServiceImpl.class.getName(),
                            BetServiceImpl.class.getName()
                    ))) {
                        shutdown();
                    }
                });
    }

    @Override
    public void shutdown() {
        log.info("shutting down");
        Flux.fromStream(
                Stream.of(
                        LambdaFunctions.PROXY_STOP,
                        LambdaFunctions.SHUTDOWN
                )
        ).delayElements(Duration.ofMinutes(1)) //review.  probably not required
                .map(LambdaFunctions::getFunctionName)
                .subscribe(lambdaFacade::invoke);

    }
}
