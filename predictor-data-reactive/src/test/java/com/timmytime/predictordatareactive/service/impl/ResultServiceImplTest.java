package com.timmytime.predictordatareactive.service.impl;

import com.timmytime.predictordatareactive.factory.MatchFactory;
import com.timmytime.predictordatareactive.model.Result;
import com.timmytime.predictordatareactive.repo.ResultRepo;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

class ResultServiceImplTest {


    private final MatchFactory matchFactory = mock(MatchFactory.class);
    private final ResultRepo resultRepo = mock(ResultRepo.class);
    private final ResultServiceImpl resultService
            = new ResultServiceImpl(resultRepo, matchFactory);

    @Test
    public void processReadyTest() throws InterruptedException {

        Result result = new Result(1);
        result.setLineup("{}");
        result.setResult("{}");

        Result result2 = new Result(2);

        result2.setLineup("{}");
        result2.setResult("{}");

        when(resultRepo.save(result)).thenReturn(Mono.just(result));
        when(resultRepo.save(result2)).thenReturn(Mono.just(result2));


        resultService.process(result);
        resultService.process(result2);


        Thread.sleep(1000L);

        verify(matchFactory, atLeastOnce()).createMatch(result2);
        verify(matchFactory, atLeastOnce()).createMatch(result);

    }

    @Test
    public void processNotReadyTest() throws InterruptedException {

        Result result = new Result(1);
        Result result2 = new Result(2);

        when(resultRepo.save(result)).thenReturn(Mono.just(result));
        when(resultRepo.save(result2)).thenReturn(Mono.just(result2));


        resultService.process(result);
        resultService.process(result2);


        Thread.sleep(1000L);

        verify(matchFactory, never()).createMatch(result2);
        verify(matchFactory, never()).createMatch(result);


    }

    @Test
    void initTest(){
        when(resultRepo.deleteAll()).thenReturn(Mono.empty());
        resultService.init();

        verify(resultRepo, atLeastOnce()).deleteAll();
    }

}