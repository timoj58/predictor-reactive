package com.timmytime.predictoreventdatareactive.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictoreventdatareactive.model.EventOdds;
import com.timmytime.predictoreventdatareactive.model.Team;
import com.timmytime.predictoreventdatareactive.service.EventOddsService;
import com.timmytime.predictoreventdatareactive.service.TeamService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BetwayServiceTest {

    private final TeamService teamService = mock(TeamService.class);
    private static final EventOddsService eventOddsService = mock(EventOddsService.class);

    private final BetwayService betwayService
            = new BetwayService(teamService, eventOddsService);

    private static JsonNode message;

    @BeforeAll
    public static void setUp() throws IOException {

        FileSystemResource fileSystemResource = new FileSystemResource("./src/main/resources/betway.json");

        message = new ObjectMapper().readTree(FileUtils.readFileToString(fileSystemResource.getFile()));

    }

    @Test
    public void newEventTest() throws InterruptedException {
        when(eventOddsService.findEvent(any(), any(), any(), any(), any()))
                .thenReturn(Mono.empty());

        when(eventOddsService.create(any())).thenReturn(Mono.just(new EventOdds()));

        Team team = new Team();
        team.setLabel("test");

        when(teamService.find(any(), any())).thenReturn(Optional.of(team));
        betwayService.receive(message);

        Thread.sleep(1000L);

        verify(eventOddsService, atLeastOnce()).create(any());
    }

    @Test
    public void onFileEventTest() throws InterruptedException {
        EventOdds eventOdds = new EventOdds();
        eventOdds.setId(UUID.randomUUID());

        when(eventOddsService.findEvent(any(), any(), any(), any(), any()))
                .thenReturn(Mono.just(eventOdds));

        Team team = new Team();
        team.setLabel("test");

        when(teamService.find(any(), any())).thenReturn(Optional.of(team));
        betwayService.receive(message);
        betwayService.receive(message);
        betwayService.receive(message);

        Thread.sleep(1000L);

        verify(eventOddsService, never()).create(any());
    }
}