package com.timmytime.predictorclientreactive.service.impl;

import com.timmytime.predictorclientreactive.facade.S3Facade;
import com.timmytime.predictorclientreactive.facade.WebClientFacade;
import com.timmytime.predictorclientreactive.model.EventOutcome;
import com.timmytime.predictorclientreactive.model.Match;
import com.timmytime.predictorclientreactive.model.MatchSelectionResponse;
import com.timmytime.predictorclientreactive.model.Team;
import com.timmytime.predictorclientreactive.service.ShutdownService;
import com.timmytime.predictorclientreactive.service.TeamService;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PreviousOutcomesServiceImplTest {

    WebClientFacade webClientFacade = mock(WebClientFacade.class);
    S3Facade s3Facade = mock(S3Facade.class);
    TeamService teamService = mock(TeamService.class);
    ShutdownService shutdownService = mock(ShutdownService.class);

   private final PreviousOutcomesServiceImpl previousOutcomesService
           = new PreviousOutcomesServiceImpl(
           "",
           "",
           0,
           webClientFacade,
           s3Facade,
           teamService,
           shutdownService
   );



   @Test
   public void loadTest() throws InterruptedException {

       when(teamService.get(any())).thenReturn(Arrays.asList(
               Team.builder().country("test").build(),
               Team.builder().country("test").build()
       ));

       when(teamService.getTeam(anyString(), any())).thenReturn(Team.builder().build());

       when(webClientFacade.getPreviousEventOutcomesByTeam(any())).thenReturn(Flux.just(
               EventOutcome.builder().date(LocalDateTime.now()).home(UUID.randomUUID()).away(UUID.randomUUID()).prediction("[]").build(),
               EventOutcome.builder().date(LocalDateTime.now().minusDays(1)).home(UUID.randomUUID()).away(UUID.randomUUID()).prediction("[]").build()
       ));

       when(webClientFacade.getMatch(anyString())).thenReturn(
               Mono.just(Match.builder().build())
       );

       previousOutcomesService.load();

       Thread.sleep(1000L);

       verify(s3Facade, atLeastOnce()).put(anyString(), anyString());
   }

}