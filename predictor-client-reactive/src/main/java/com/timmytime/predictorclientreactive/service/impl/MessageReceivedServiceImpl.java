package com.timmytime.predictorclientreactive.service.impl;

import com.timmytime.predictorclientreactive.enumerator.Messages;
import com.timmytime.predictorclientreactive.request.Message;
import com.timmytime.predictorclientreactive.service.ILoadService;
import com.timmytime.predictorclientreactive.service.MessageReceivedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service("messageReceivedService")
public class MessageReceivedServiceImpl implements MessageReceivedService {

    private final Logger log = LoggerFactory.getLogger(MessageReceivedServiceImpl.class);

    private final List<ILoadService> loaders = new ArrayList<>();
    private final List<Messages> received = new ArrayList<>();

    @Autowired
    public MessageReceivedServiceImpl(
            CompetitionServiceImpl competitionService,
            BetServiceImpl betService,
            FixtureServiceImpl fixtureService,
            PreviousFixtureServiceImpl previousFixtureService,
            PlayersMatchServiceImpl playersMatchService,
            TeamsMatchServiceImpl teamsMatchService,
            PreviousOutcomesServiceImpl previousOutcomesService
    ) {
        this.loaders.add(competitionService);
        this.loaders.add(betService);
        this.loaders.add(fixtureService);
        this.loaders.add(previousFixtureService);
        this.loaders.add(playersMatchService);
        this.loaders.add(teamsMatchService);
        this.loaders.add(previousOutcomesService);

    }


    @Override
    public Mono<Void> receive(Mono<Message> message) {

        return message.doOnNext(
                msg -> {
                    log.info("received {}", msg.getType());
                    received.add(msg.getType());

                    if (ready()) {
                        log.info("all messages received");
                        CompletableFuture.runAsync(() -> load());
                    }
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
}
