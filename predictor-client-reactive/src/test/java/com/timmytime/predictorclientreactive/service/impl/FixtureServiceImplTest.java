package com.timmytime.predictorclientreactive.service.impl;

import com.timmytime.predictorclientreactive.facade.S3Facade;
import com.timmytime.predictorclientreactive.facade.WebClientFacade;
import com.timmytime.predictorclientreactive.model.Event;
import com.timmytime.predictorclientreactive.model.Team;
import com.timmytime.predictorclientreactive.service.ShutdownService;
import com.timmytime.predictorclientreactive.service.TeamService;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FixtureServiceImplTest {

    private final S3Facade s3Facade = mock(S3Facade.class);
    private final WebClientFacade webClientFacade = mock(WebClientFacade.class);
    private final ShutdownService shutdownService = mock(ShutdownService.class);
    private final TeamService teamService = mock(TeamService.class);

    private final FixtureServiceImpl fixtureService
            = new FixtureServiceImpl(
                    "", s3Facade, webClientFacade,shutdownService, teamService
    );

    @Test
    public void loadTest(){

        Event event = new Event();
        event.setDate(LocalDateTime.now());
        event.setAway(UUID.randomUUID());
        event.setHome(UUID.randomUUID());

        when(teamService.getTeam(anyString(), any(UUID.class))).thenReturn(new Team());
        when(webClientFacade.getEvents(anyString())).thenReturn(Flux.fromStream(Arrays.asList(event).stream()));

        fixtureService.load();

        verify(s3Facade, atLeastOnce()).put("", "");
        verify(shutdownService, atLeastOnce()).receive(anyString());
    }

}