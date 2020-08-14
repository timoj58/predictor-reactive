package com.timmytime.predictorclientreactive.service.impl;

import com.timmytime.predictorclientreactive.enumerator.Messages;
import com.timmytime.predictorclientreactive.request.Message;
import com.timmytime.predictorclientreactive.service.ILoadService;
import com.timmytime.predictorclientreactive.service.MessageReceivedService;
import com.timmytime.predictorclientreactive.enumerator.CountryCompetitions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Service("messageReceivedService")
public class MessageReceivedServiceImpl implements MessageReceivedService {

    private final Logger log = LoggerFactory.getLogger(MessageReceivedServiceImpl.class);

    private final List<ILoadService> loaders = new ArrayList<>();
    private final Map<String, List<Messages>> received = new HashMap<>();

    @Autowired
    public MessageReceivedServiceImpl(
            CompetitionServiceImpl competitionService,
            BetServiceImpl betService,
            FixtureServiceImpl fixtureService,
            PreviousFixtureServiceImpl previousFixtureService,
            PlayersMatchServiceImpl matchService,
            TeamsMatchServiceImpl teamsMatchService
    ){
        this.loaders.add(competitionService);
       // this.loaders.add(betService);
        this.loaders.add(fixtureService);
        this.loaders.add(previousFixtureService);
       // this.loaders.add(matchService);
        this.loaders.add(teamsMatchService);

        Arrays.asList(
                CountryCompetitions.values()
        ).stream()
                .forEach(country -> received.put(country.name(), new ArrayList<>()));
    }


    @Override
    public Mono<Void> receive(Mono<Message> message) {

        return message.doOnNext(
                msg -> {
                    log.info("received {}", msg.getType());
                    received.get(msg.getCountry()).add(msg.getType());

                    if(ready()){
                        log.info("all messages received");
                        load();
                    }
                }
        ).thenEmpty(Mono.empty());

    }

    @Override
    public Mono<Void> test() {
        load();
        return Mono.empty();
    }

    private void load(){
        log.info("loading");
        Flux.fromStream(
                loaders.stream()
        ).subscribe(ILoadService::load);
    }

    private Boolean ready(){
        Iterator<String> keys = received.keySet().iterator();
        while (keys.hasNext()){
            if(!received.get(keys.next()).containsAll(Arrays.asList(Messages.values()))){
                return Boolean.FALSE;
            }
        }

        return Boolean.TRUE;
    }
}
