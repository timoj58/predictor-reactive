package com.timmytime.predictorclientreactive.service.impl;

import com.timmytime.predictorclientreactive.enumerator.Messages;
import com.timmytime.predictorclientreactive.request.Message;
import com.timmytime.predictorclientreactive.enumerator.CountryCompetitions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static org.mockito.Mockito.*;

@Disabled
class MessageReceivedServiceImplTest {

    CompetitionServiceImpl competitionService = mock(CompetitionServiceImpl.class);
    BetServiceImpl betService = mock(BetServiceImpl.class);
    FixtureServiceImpl fixtureService = mock(FixtureServiceImpl.class);
    PreviousFixtureServiceImpl previousFixtureService = mock(PreviousFixtureServiceImpl.class);
    PlayersMatchServiceImpl matchService = mock(PlayersMatchServiceImpl.class);
    TeamsMatchServiceImpl teamsMatchService = mock(TeamsMatchServiceImpl.class);
    PreviousOutcomesServiceImpl previousOutcomesService = mock(PreviousOutcomesServiceImpl.class);

    private final MessageReceivedServiceImpl messageReceivedService = new MessageReceivedServiceImpl(
            competitionService,
            betService,
            fixtureService,
            previousFixtureService,
            matchService,
            teamsMatchService,
            previousOutcomesService
    );

    @Test
    public void readyTest() throws InterruptedException {

        Arrays.asList(
                CountryCompetitions.values()
        ).stream()
                .forEach(country -> {
                    Message message = new Message();
                    message.setType(Messages.MATCH_PREDICTIONS);

                    messageReceivedService.receive(Mono.just(message)).subscribe();

                    message.setType(Messages.PLAYER_PREDICTIONS);

                    messageReceivedService.receive(Mono.just(message)).subscribe();

                });


        Thread.sleep(1000L);


        verify(competitionService, atLeastOnce()).load();


    }

    @Test
    public void notReadyTest() throws InterruptedException {
        Message message = new Message();
        message.setType(Messages.MATCH_PREDICTIONS);

        messageReceivedService.receive(Mono.just(message)).subscribe();

        Thread.sleep(1000L);

        verify(competitionService, never()).load();
    }

}