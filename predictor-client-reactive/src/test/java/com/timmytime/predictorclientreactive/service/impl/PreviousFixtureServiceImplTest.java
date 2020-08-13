package com.timmytime.predictorclientreactive.service.impl;

import com.timmytime.predictorclientreactive.facade.S3Facade;
import com.timmytime.predictorclientreactive.facade.WebClientFacade;
import com.timmytime.predictorclientreactive.model.EventOutcome;
import com.timmytime.predictorclientreactive.model.Match;
import com.timmytime.predictorclientreactive.service.ShutdownService;
import com.timmytime.predictorclientreactive.service.TeamService;
import com.timmytime.predictorclientreactive.enumerator.CountryCompetitions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class PreviousFixtureServiceImplTest {

    private final S3Facade s3Facade = mock(S3Facade.class);
    private final WebClientFacade webClientFacade = mock(WebClientFacade.class);
    private final ShutdownService shutdownService=  mock(ShutdownService.class);
    private final TeamService teamService =  mock(TeamService.class);

    private final PreviousFixtureServiceImpl previousFixtureService = new PreviousFixtureServiceImpl(
            "", "", 0,
            s3Facade, webClientFacade, shutdownService, teamService
    );

    Supplier<List<EventOutcome>> get = () ->  Arrays.asList(new EventOutcome());


    @Test
    public void loadTest() throws InterruptedException {


       Arrays.asList(
               CountryCompetitions.values()
       )
               .stream()
               .map(f -> f.getCompetitions())
               .flatMap(Collection::stream)
               .collect(Collectors.toList())
               .stream()
               .forEach(c ->
                       when(webClientFacade.getPreviousEvents("/previous-events/"+c))
                               .thenReturn(Flux.fromStream(get.get().stream()
                               )));



        when(webClientFacade.getMatch(anyString())).thenReturn(Mono.just(new Match()));

        previousFixtureService.load();

        Thread.sleep(1000L);

        verify(s3Facade, atLeast(15)).put(anyString(), anyString());
        verify(shutdownService, atMostOnce()).receive(anyString());
    }

}