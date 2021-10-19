package com.timmytime.predictordatareactive.service.impl;

import com.timmytime.predictordatareactive.factory.MatchFactory;
import com.timmytime.predictordatareactive.model.Result;
import com.timmytime.predictordatareactive.repo.ResultRepo;
import com.timmytime.predictordatareactive.service.ResultService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.function.Consumer;

@Slf4j
@Service("resultService")
public class ResultServiceImpl implements ResultService {

    private final ResultRepo resultRepo;
    private Consumer<Result> receive;

    @Autowired
    public ResultServiceImpl(
            ResultRepo resultRepo,
            MatchFactory matchFactory
    ) {
        this.resultRepo = resultRepo;

        Flux<Result> results = Flux.push(sink ->
                ResultServiceImpl.this.receive = sink::next, FluxSink.OverflowStrategy.BUFFER);

        results.subscribe(matchFactory::createMatch);
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
        return resultRepo.findById(matchId) //need to handle not found case...
                .switchIfEmpty(resultRepo.save(new Result(matchId)));
    }

    @Override
    public void repair() {
        resultRepo.findByProcessed(Boolean.FALSE)
                .limitRate(10)
                .subscribe(result -> {
                      result.setLineup(new JSONObject()
                              .put("data", new JSONObject()
                                      .put("home", new JSONArray())
                                      .put("away", new JSONArray())
                              ).toString());

                     process(result);
                });
    }

    @PostConstruct
    private void init() {
        resultRepo.deleteAll().subscribe();
    }


}
