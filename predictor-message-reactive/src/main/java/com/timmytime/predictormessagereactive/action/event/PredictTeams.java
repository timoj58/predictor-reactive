package com.timmytime.predictormessagereactive.action.event;

import com.timmytime.predictormessagereactive.action.EventAction;
import com.timmytime.predictormessagereactive.action.handler.PredictionHandler;
import com.timmytime.predictormessagereactive.configuration.HostsConfiguration;
import com.timmytime.predictormessagereactive.enumerator.Action;
import com.timmytime.predictormessagereactive.enumerator.Event;
import com.timmytime.predictormessagereactive.enumerator.EventType;
import com.timmytime.predictormessagereactive.facade.WebClientFacade;
import com.timmytime.predictormessagereactive.request.Message;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PredictTeams implements IEventAction {

    private final List<Event> TEAM_PREDICTION_EVENTS = Arrays.asList(Event.TEAMS_TRAINED, Event.EVENTS_LOADED);

    private final Integer delay;
    private final WebClientFacade webClientFacade;
    private final HostsConfiguration hostsConfiguration;

    @Autowired
    public PredictTeams(
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
        return Pair.of(
                Action.PREDICT_TEAMS,
                EventAction.builder()
                        .processed(Boolean.FALSE)
                        //input all countries trained, countries
                        .handler((ce, ae) ->
                                new PredictionHandler().predictions(
                                        Triple.of(
                                                ce.stream().map(m -> m.getMessage().getEventType()).collect(Collectors.toList()),
                                                TEAM_PREDICTION_EVENTS,
                                                EventType.countriesAndCompetitions()
                                        ),
                                        Pair.of(delay, (c) -> webClientFacade.predict(
                                                hostsConfiguration.getTeamEvents() + "/message",
                                                Message.builder()
                                                        .event(Event.TEAMS_PREDICTED)
                                                        .eventType(c)
                                                        .build()
                                        ))))
                        .build()
        );
    }
}
