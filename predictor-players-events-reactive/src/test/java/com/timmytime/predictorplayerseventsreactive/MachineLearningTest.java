package com.timmytime.predictorplayerseventsreactive;

import com.timmytime.predictorplayerseventsreactive.facade.WebClientFacade;
import com.timmytime.predictorplayerseventsreactive.model.Event;
import com.timmytime.predictorplayerseventsreactive.model.FantasyOutcome;
import com.timmytime.predictorplayerseventsreactive.service.*;
import com.timmytime.predictorplayerseventsreactive.service.impl.PlayerServiceImpl;
import com.timmytime.predictorplayerseventsreactive.service.impl.PredictionServiceImpl;
import com.timmytime.predictorplayerseventsreactive.service.impl.TensorflowPredictionServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Disabled
public class MachineLearningTest {

    private static final FantasyOutcomeService fantasyEventOutcomeService = mock(FantasyOutcomeService.class);
    private static final EventsService eventsService = mock(EventsService.class);
    private static final PlayerResponseService playerResponseService = mock(PlayerResponseService.class);
    private static final PlayerService playerService = new PlayerServiceImpl("http://localhost:8092", new WebClientFacade());
    private static final TensorflowPredictionService tensorflowPredictionService =
            new TensorflowPredictionServiceImpl(
                    "ec2-54-162-22-91.compute-1.amazonaws.com:5000",
                    "/predict/goals/<init>/<receipt>",
                    "/predict/assists/<init>/<receipt>",
                    "/predict/yellow-card/<init>/<receipt>",
                    "/predict/init/<type>",
                    "/predict/clear-down/<type>",
                    new WebClientFacade()
            );

    private final PredictionServiceImpl predictionService
            = new PredictionServiceImpl(
            eventsService,
            playerService,
            tensorflowPredictionService,
            fantasyEventOutcomeService
    );

    @BeforeAll
    public static void setup() throws InterruptedException {


        playerService.load();

     /*   tensorflowPredictionService.init("goals");
        tensorflowPredictionService.init("assists");
        tensorflowPredictionService.init("conceded");
        tensorflowPredictionService.init("minutes");
        tensorflowPredictionService.init("red");
        tensorflowPredictionService.init("yellow");
        tensorflowPredictionService.init("saves");

      */

        Thread.sleep(10000L);

    }

    @Test
    public void test() throws InterruptedException {

        FantasyOutcome fantasyOutcome = new FantasyOutcome();
        fantasyOutcome.setHome("home");
        fantasyOutcome.setId(UUID.randomUUID());

        when(fantasyEventOutcomeService.save(any()))
                .thenReturn(Mono.just(fantasyOutcome));

        Event event = new Event();
        event.setDate(LocalDateTime.now());
        event.setHome(UUID.fromString("fbb0461b-722a-4328-bb35-9518ce512899"));
        event.setAway(UUID.fromString("f2c1cef6-90db-4e7a-926f-f726b7f75780"));

        when(eventsService.get(any()))
                .thenReturn(Flux.fromStream(
                        Arrays.asList(event).stream()
                ));


        predictionService.start("ENGLAND");

        Thread.sleep(10000L);

    }

}
