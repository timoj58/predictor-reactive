package com.timmytime.predictordatareactive.service.impl;

import com.timmytime.predictordatareactive.factory.MatchFactory;
import com.timmytime.predictordatareactive.model.Match;
import com.timmytime.predictordatareactive.model.Result;
import com.timmytime.predictordatareactive.repo.ResultRepo;
import com.timmytime.predictordatareactive.service.ResultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Service("resultService")
public class ResultServiceImpl implements ResultService {

    private final Logger log = LoggerFactory.getLogger(ResultServiceImpl.class);

    private final ResultRepo resultRepo;

    private Consumer<Result> receive;
    private final Flux<Result> results;

    @Autowired
    public ResultServiceImpl(
            ResultRepo resultRepo,
            MatchFactory matchFactory
    ){
        this.resultRepo = resultRepo;

        this.results = Flux.push(sink ->
            ResultServiceImpl.this.receive = (t) -> sink.next(t), FluxSink.OverflowStrategy.BUFFER);

        this.results.subscribe(matchFactory::createMatch);
    }

    @Override
    public void process(Result result) {

        resultRepo.save(result).subscribe(saved -> {
                        log.info("saved result record {}, ready? {}", saved.getMatchId(), saved.ready());
                        if (saved.ready()) {
                            log.info("processing");
                            receive.accept(result);
                        }
                    });

    }

    @Override
    public Mono<Result> findByMatch(Integer matchId) {
        return  resultRepo.findById(matchId) //need to handle not found case...
                .switchIfEmpty(resultRepo.save(new Result(matchId)));
    }

    @PostConstruct
    private void init(){
        resultRepo.deleteAll().subscribe();
    }


}
