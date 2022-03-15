package com.timmytime.predictormessagereactive.action.event;

import com.timmytime.predictormessagereactive.action.EventAction;
import com.timmytime.predictormessagereactive.action.handler.TrainingHandler;
import com.timmytime.predictormessagereactive.configuration.HostsConfiguration;
import com.timmytime.predictormessagereactive.enumerator.Action;
import com.timmytime.predictormessagereactive.enumerator.Event;
import com.timmytime.predictormessagereactive.enumerator.EventType;
import com.timmytime.predictormessagereactive.facade.WebClientFacade;
import com.timmytime.predictormessagereactive.request.Message;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TrainPlayers implements IEventAction {

    private final WebClientFacade webClientFacade;
    private final HostsConfiguration hostsConfiguration;

    @Override
    public Pair<Action, EventAction> create() {
        return Pair.of(Action.TRAIN_PLAYERS,
                EventAction.builder()
                        .processed(Boolean.FALSE)
                        //input data loaded for all competitions, output -> train all
                        .handler((ce, ae) -> new TrainingHandler().training(
                                ce.stream().map(m -> m.getMessage().getEventType()).collect(Collectors.toList()),
                                () -> webClientFacade.train(hostsConfiguration.getPlayers() + "/message",
                                        Message.builder()
                                                .event(Event.PLAYERS_TRAINED)
                                                .eventType(EventType.ALL)
                                                .build()
                                )))
                        .build());
    }
}
