package com.timmytime.predictorclientreactive.service.impl;

import com.timmytime.predictorclientreactive.enumerator.LambdaFunctions;
import com.timmytime.predictorclientreactive.enumerator.Messages;
import com.timmytime.predictorclientreactive.facade.LambdaFacade;
import com.timmytime.predictorclientreactive.request.Message;
import com.timmytime.predictorclientreactive.service.ILoadService;
import com.timmytime.predictorclientreactive.service.MessageReceivedService;
import com.timmytime.predictorclientreactive.service.PlayerService;
import com.timmytime.predictorclientreactive.service.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.runAsync;
import static reactor.core.publisher.Flux.fromStream;

@Slf4j
@Service("messageReceivedService")
public class MessageReceivedServiceImpl implements MessageReceivedService {

    private final List<ILoadService> loaders = new ArrayList<>();
    private final List<Messages> received = new ArrayList<>();

    private final TeamService teamService;
    private final PlayerService playerService;
    private final LambdaFacade lambdaFacade;

    @Autowired
    public MessageReceivedServiceImpl(
            Collection<ILoadService> loaders,
            LambdaFacade lambdaFacade,
            TeamService teamService,
            PlayerService playerService
    ) {
        this.loaders.addAll(loaders);
        this.lambdaFacade = lambdaFacade;
        this.teamService = teamService;
        this.playerService = playerService;
    }


    @Override
    public Mono<Void> receive(Mono<Message> message) {

        return message.doOnNext(
                msg -> {
                    log.info("received {}", msg.getEvent());
                    runAsync(() -> process(Messages.valueOf(msg.getEvent())))
                            .thenRun(() -> {
                                if (ready()) {
                                    log.info("all messages received");
                                    runAsync(this::load);
                                }
                            });
                }
        ).thenEmpty(Mono.empty());

    }

    private void load() {
        log.info("loading...");
        CompletableFuture.runAsync(teamService::load)
                .thenRun(playerService::load)
                .thenRun(() ->
                        Mono.just(loaders.stream())
                                .delayElement(Duration.ofMinutes(1))
                                .subscribe(stream -> fromStream(stream).subscribe(ILoadService::load)));
    }

    private Boolean ready() {
        return received.containsAll(Arrays.asList(Messages.values()));
    }

    private void process(Messages msg) {
        log.info("processing {}", msg);
        received.add(msg);

        switch (msg) {
            case TEAMS_PREDICTED:
                lambdaFacade.invoke(LambdaFunctions.SHUTDOWN_ML_TEAMS.getFunctionName());
                break;
            case PLAYERS_PREDICTED:
                lambdaFacade.invoke(LambdaFunctions.SHUTDOWN_ML_PLAYERS.getFunctionName());
                break;
        }
    }
}
