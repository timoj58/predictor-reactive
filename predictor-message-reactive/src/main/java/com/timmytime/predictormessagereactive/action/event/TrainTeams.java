package com.timmytime.predictormessagereactive.action.event;

import com.timmytime.predictormessagereactive.action.EventAction;
import com.timmytime.predictormessagereactive.action.handler.TrainingHandler;
import com.timmytime.predictormessagereactive.configuration.HostsConfiguration;
import com.timmytime.predictormessagereactive.enumerator.Action;
import com.timmytime.predictormessagereactive.enumerator.Event;
import com.timmytime.predictormessagereactive.enumerator.EventType;
import com.timmytime.predictormessagereactive.facade.WebClientFacade;
import com.timmytime.predictormessagereactive.request.Message;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.stream.Collectors;

public class TrainTeams implements IEventAction {

    private final Integer delay;
    private final WebClientFacade webClientFacade;
    private final HostsConfiguration hostsConfiguration;

    @Autowired
    public TrainTeams(
            @Value("${test.delay}") Integer delay,
            WebClientFacade webClientFacade,
            HostsConfiguration hostsConfiguration
    ) {
        this.delay = delay;
        this.webClientFacade = webClientFacade;
        this.hostsConfiguration = hostsConfiguration;
    }

    @Override
    public Pair<Action, EventAction> create() {
        return Pair.of(Action.TRAIN_TEAMS, EventAction.builder()
                .processed(Boolean.FALSE)
                .handler((ce, ae) -> new TrainingHandler().training(
                        ce.stream().map(m -> m.getMessage().getEventType()).collect(Collectors.toList()),
                        () -> Flux.fromStream(EventType.countries().stream())
                                .limitRate(1)
                                .delayElements(Duration.ofSeconds(delay))
                                .subscribe(country ->
                                        webClientFacade.train(hostsConfiguration.getTeams() + "/message",
                                                Message.builder()
                                                        .event(Event.TEAMS_TRAINED)
                                                        .eventType(country)
                                                        .build()))))
                .build());
    }
}
