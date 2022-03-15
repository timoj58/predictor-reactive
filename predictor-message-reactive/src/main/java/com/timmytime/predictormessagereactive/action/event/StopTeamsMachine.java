package com.timmytime.predictormessagereactive.action.event;

import com.timmytime.predictormessagereactive.action.EventAction;
import com.timmytime.predictormessagereactive.configuration.HostsConfiguration;
import com.timmytime.predictormessagereactive.enumerator.Action;
import com.timmytime.predictormessagereactive.enumerator.Event;
import com.timmytime.predictormessagereactive.facade.WebClientFacade;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StopTeamsMachine extends StopMachine implements IEventAction {
    @Autowired
    public StopTeamsMachine(WebClientFacade webClientFacade, HostsConfiguration hostsConfiguration) {
        super(webClientFacade, hostsConfiguration);
    }

    @Override
    public Pair<Action, EventAction> create() {
        return Pair.of(
                Action.STOP_TEAM_MACHINE,
                EventAction.builder()
                        .processed(Boolean.FALSE)
                        .handler((ce, ae) -> this.stop(ce, Event.TEAMS_PREDICTED))
                        .build()
        );
    }
}
