package com.timmytime.predictorclientreactive.service.impl;

import com.timmytime.predictorclientreactive.enumerator.CountryCompetitions;
import com.timmytime.predictorclientreactive.facade.S3Facade;
import com.timmytime.predictorclientreactive.facade.WebClientFacade;
import com.timmytime.predictorclientreactive.model.EventOutcome;
import com.timmytime.predictorclientreactive.model.Team;
import com.timmytime.predictorclientreactive.service.ShutdownService;
import com.timmytime.predictorclientreactive.service.TeamService;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.Mockito.*;

//@Disabled
class TeamsMatchServiceImplTest {

    WebClientFacade webClientFacade = mock(WebClientFacade.class);
    S3Facade s3Facade = mock(S3Facade.class);
    TeamService teamService = mock(TeamService.class);
    ShutdownService shutdownService = mock(ShutdownService.class);

    private final TeamsMatchServiceImpl teamsMatchService
            = new TeamsMatchServiceImpl(
            "", 0, webClientFacade, s3Facade, teamService, shutdownService
    );

    @Test
    public void loadTest() throws InterruptedException {

        List<EventOutcome> eventOutcomeList = new ArrayList<>();

        IntStream.range(0, 20).forEach(i -> eventOutcomeList.add(EventOutcome.builder()
                .date(LocalDateTime.now())
                .prediction("{}").build()));

        Arrays.asList(
                        CountryCompetitions.values()
                )
                .stream()
                .map(f -> f.getCompetitions())
                .flatMap(Collection::stream)
                .collect(Collectors.toList())
                .stream()
                .forEach(c ->
                        when(webClientFacade.getUpcomingEventOutcomes("/events/" + c))
                                .thenReturn(Flux.fromStream(eventOutcomeList.stream()
                                )));


        when(teamService.getTeam(any(), any())).thenReturn(
                new Team()
        );


        teamsMatchService.load();

        Thread.sleep(3000L);


        verify(s3Facade, atLeast(15)).put(anyString(), anyString());
        verify(shutdownService, atLeastOnce()).receive(anyString());

    }

}