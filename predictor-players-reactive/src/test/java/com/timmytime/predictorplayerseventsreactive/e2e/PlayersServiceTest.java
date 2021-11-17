package com.timmytime.predictorplayerseventsreactive.e2e;

import com.timmytime.predictorplayerseventsreactive.enumerator.CountryCompetitions;
import com.timmytime.predictorplayerseventsreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayerseventsreactive.facade.WebClientFacade;
import com.timmytime.predictorplayerseventsreactive.model.*;
import com.timmytime.predictorplayerseventsreactive.repo.PlayerMatchRepo;
import com.timmytime.predictorplayerseventsreactive.repo.PlayersTrainingHistoryRepo;
import com.timmytime.predictorplayerseventsreactive.request.Message;
import com.timmytime.predictorplayerseventsreactive.service.*;
import com.timmytime.predictorplayerseventsreactive.service.impl.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PlayersServiceTest {

    private final PlayersTrainingHistoryRepo playersTrainingHistoryRepo = mock(PlayersTrainingHistoryRepo.class);
    private final PlayerMatchRepo playerMatchRepo = mock(PlayerMatchRepo.class);
    private final WebClientFacade webClientFacade = mock(WebClientFacade.class);
    private final PlayersTrainingHistoryService playersTrainingHistoryService
            = new PlayersTrainingHistoryServiceImpl(playersTrainingHistoryRepo);

    private final TensorflowTrainingService tensorflowTrainingService
            = new TensorflowTrainingServiceImpl("training", "goals", "assists", "yellows",
            playersTrainingHistoryService, webClientFacade);
    private final TrainingService trainingService
            = new TrainingServiceImpl(0, playersTrainingHistoryService, tensorflowTrainingService);
    private final PlayerMatchService playerMatchService
            = new PlayerMatchServiceImpl("data", webClientFacade);
    private final TensorflowDataService tensorflowDataService
            = new TensorflowDataServiceImpl(playerMatchRepo);
    private final TrainingModelService trainingModelService
            = new TrainingModelServiceImpl("data", true, 0, 0,
            webClientFacade, playersTrainingHistoryService, playerMatchService, trainingService, tensorflowDataService);

    private final MessageReceivedService messageReceivedService
            = new MessageReceivedServiceImpl(trainingService, trainingModelService, playersTrainingHistoryService);

    @BeforeEach
    void setup(){
        var history = PlayersTrainingHistory.builder()
                .id(UUID.randomUUID())
                .type(FantasyEventTypes.GOALS)
                .toDate(LocalDateTime.now().minusDays(1))
                .fromDate(LocalDateTime.now().minusDays(2)).build();

        when(playersTrainingHistoryRepo.findFirstByTypeOrderByDateDesc(any(FantasyEventTypes.class)))
                .thenReturn(history);

        when(playersTrainingHistoryRepo.save(any())).thenReturn(history);

        when(playersTrainingHistoryRepo.findById(any())).thenReturn(Optional.of(history));

        when(webClientFacade.getPlayers(anyString())).thenReturn(
                Flux.just(Player.builder()
                        .lastAppearance(LocalDate.now().minusDays(1)).build())
        );

    }

    @Test
    void train() throws InterruptedException {


        when(playerMatchRepo.findByDateBetween(any(), any()))
                .thenReturn(Arrays.asList(PlayerMatch.builder().build()));

        Stream.of(
                CountryCompetitions.values()
        ).forEach(country ->
                country.getCompetitions().forEach(competition ->
                        messageReceivedService.receive(
                                Mono.just(Message.builder()
                                        .competition(competition)
                                        .country(country.name()).build())
                        ).subscribe()
                ));


        Thread.sleep(250);
        verify(webClientFacade, atLeastOnce()).train(anyString());

    }

    @Test
    void create() throws InterruptedException {
        when(webClientFacade.getAppearances(anyString())).thenReturn(
                Flux.just(LineupPlayer.builder()
                        .date(LocalDateTime.now().plusDays(1))
                        .teamId(UUID.randomUUID()).build())
        );

        when(webClientFacade.getMatch(anyString())).thenReturn(
                Mono.just(Match.builder()
                        .awayScore(1)
                        .homeScore(1)
                        .date(LocalDateTime.now())
                        .awayTeam(UUID.randomUUID())
                        .awayTeam(UUID.randomUUID()).build())
        );

        when(webClientFacade.getStats(anyString())).thenReturn(
                Flux.just(StatMetric.builder().build())
        );
        messageReceivedService.createTrainingModel().subscribe();

        Thread.sleep(250);
        verify(webClientFacade, atLeastOnce()).train(anyString());

    }
}
