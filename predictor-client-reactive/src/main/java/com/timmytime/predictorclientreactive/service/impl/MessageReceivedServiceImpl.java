package com.timmytime.predictorclientreactive.service.impl;

import com.timmytime.predictorclientreactive.enumerator.LambdaFunctions;
import com.timmytime.predictorclientreactive.enumerator.Messages;
import com.timmytime.predictorclientreactive.facade.LambdaFacade;
import com.timmytime.predictorclientreactive.request.Message;
import com.timmytime.predictorclientreactive.service.ILoadService;
import com.timmytime.predictorclientreactive.service.MessageReceivedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service("messageReceivedService")
public class MessageReceivedServiceImpl implements MessageReceivedService {

    private final List<ILoadService> loaders = new ArrayList<>();
    private final List<Messages> received = new ArrayList<>();

    private final LambdaFacade lambdaFacade;

    @Autowired
    public MessageReceivedServiceImpl(
            CompetitionServiceImpl competitionService,
            FixtureServiceImpl fixtureService,
            PreviousFixtureServiceImpl previousFixtureService,
            PlayersMatchServiceImpl playersMatchService,
            TeamsMatchServiceImpl teamsMatchService,
            PreviousOutcomesServiceImpl previousOutcomesService,
            LambdaFacade lambdaFacade
    ) {
        this.loaders.add(competitionService);
        this.loaders.add(fixtureService);
        this.loaders.add(previousFixtureService);
        this.loaders.add(playersMatchService);
        this.loaders.add(teamsMatchService);
        this.loaders.add(previousOutcomesService);

        this.lambdaFacade = lambdaFacade;

    }


    @Override
    public Mono<Void> receive(Mono<Message> message) {

        return message.doOnNext(
                msg -> {
                    log.info("received {}", msg.getType());
                    CompletableFuture.runAsync(() -> process(msg.getType()))
                            .thenRun(() -> {
                                if (ready()) {
                                    log.info("all messages received");
                                    CompletableFuture.runAsync(() -> load());
                                }
                            });
                }
        ).thenEmpty(Mono.empty());

    }

    @Override
    public Mono<Void> test() {
        load();
        return Mono.empty();
    }

    private void load() {
        log.info("loading");
        Flux.fromStream(
                loaders.stream()
        ).subscribe(ILoadService::load);
    }

    private Boolean ready() {
        return received.containsAll(Arrays.asList(Messages.values()));
    }

    private void process(Messages msg){
        received.add(msg);

        switch (msg){
            case MATCH_PREDICTIONS:
                lambdaFacade.invoke(LambdaFunctions.SHUTDOWN_ML_TEAMS.getFunctionName());
                break;
            case PLAYER_PREDICTIONS:
                lambdaFacade.invoke(LambdaFunctions.SHUTDOWN_ML_PLAYERS.getFunctionName());
                break;
        }
    }
}
