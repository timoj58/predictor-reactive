package com.timmytime.predictorclientreactive.service.impl;

import com.timmytime.predictorclientreactive.request.Message;
import com.timmytime.predictorclientreactive.service.ILoadService;
import com.timmytime.predictorclientreactive.service.MessageReceivedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service("messageReceivedService")
public class MessageReceivedServiceImpl implements MessageReceivedService {

    private final List<ILoadService> loaders = new ArrayList<>();

    @Autowired
    public MessageReceivedServiceImpl(
            CompetitionServiceImpl competitionService,
            PlayerServiceImpl playerService,
            BetServiceImpl betService,
            FixtureServiceImpl fixtureService,
            PreviousFixtureServiceImpl previousFixtureService,
            MatchServiceImpl matchService
    ){
        this.loaders.add(competitionService);
        this.loaders.add(playerService);
        this.loaders.add(betService);
        this.loaders.add(fixtureService);
        this.loaders.add(previousFixtureService);
        this.loaders.add(matchService);
    }


    @Override
    public Mono<Void> receive(Mono<Message> message) {
        /*
          TBC likely just that all the predictions have completed.
          so for each country, and for players.
         */

        return null;
    }

    private void load(){
        Flux.fromStream(
                loaders.stream()
        ).subscribe(ILoadService::load);
    }
}
