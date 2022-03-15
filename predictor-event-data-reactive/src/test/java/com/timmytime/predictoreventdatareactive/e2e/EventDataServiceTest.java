package com.timmytime.predictoreventdatareactive.e2e;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictoreventdatareactive.enumerator.Providers;
import com.timmytime.predictoreventdatareactive.facade.WebClientFacade;
import com.timmytime.predictoreventdatareactive.model.EventOdds;
import com.timmytime.predictoreventdatareactive.model.MatchTeams;
import com.timmytime.predictoreventdatareactive.model.Team;
import com.timmytime.predictoreventdatareactive.repo.EventOddsRepo;
import com.timmytime.predictoreventdatareactive.service.EspnService;
import com.timmytime.predictoreventdatareactive.service.EventOddsService;
import com.timmytime.predictoreventdatareactive.service.MessageReceivedService;
import com.timmytime.predictoreventdatareactive.service.ProviderService;
import com.timmytime.predictoreventdatareactive.service.impl.EventOddsServiceImpl;
import com.timmytime.predictoreventdatareactive.service.impl.MessageReceivedServiceImpl;
import com.timmytime.predictoreventdatareactive.service.impl.ProviderServiceImpl;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class EventDataServiceTest {

    private final WebClientFacade webClientFacade = mock(WebClientFacade.class);
    private final EventOddsRepo eventOddsRepo = mock(EventOddsRepo.class);
    private final EventOddsService eventOddsService = new EventOddsServiceImpl(eventOddsRepo);

    private final EspnService espnService = new EspnService(
            "host", webClientFacade, eventOddsService
    );

    private final ProviderService providerService
            = new ProviderServiceImpl(espnService);

    private final MessageReceivedService messageReceivedService
            = new MessageReceivedServiceImpl(providerService);

    @Test
    void smokeTest() throws InterruptedException, JSONException, JsonProcessingException {

        when(eventOddsRepo.save(any())).thenReturn(
                Mono.just(EventOdds.builder().build())
        );
        when(webClientFacade.getMatchTeams(any()))
                .thenReturn(Mono.just(
                        MatchTeams.builder()
                                .away(Optional.of(Team.builder().build()))
                                .home(Optional.of(Team.builder().build()))
                                .build()
                ));

        messageReceivedService.receive(
                Mono.just(
                        new ObjectMapper().readTree(
                                new JSONObject().put("provider", Providers.ESPN_ODDS.name())
                                        .put("competition", "england_1")
                                        .put("data", new JSONObject()
                                                        .put("home", "AFC Bournemouth")
                                                        .put("away", "Liverpool")
                                                        .put("milliseconds", LocalDateTime.now().toEpochSecond(
                                                                ZoneOffset.UTC
                                                        ))
                                                ).toString()
                        )
                )
        ).subscribe();

        Thread.sleep(1000);

        verify(eventOddsRepo, atLeastOnce()).save(any());

    }
}
