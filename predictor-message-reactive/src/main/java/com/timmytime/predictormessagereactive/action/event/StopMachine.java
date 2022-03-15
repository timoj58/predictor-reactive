package com.timmytime.predictormessagereactive.action.event;

import com.timmytime.predictormessagereactive.configuration.HostsConfiguration;
import com.timmytime.predictormessagereactive.enumerator.Event;
import com.timmytime.predictormessagereactive.enumerator.EventType;
import com.timmytime.predictormessagereactive.facade.WebClientFacade;
import com.timmytime.predictormessagereactive.model.CycleEvent;
import com.timmytime.predictormessagereactive.request.Message;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public abstract class StopMachine {

    private final WebClientFacade webClientFacade;
    private final HostsConfiguration hostsConfiguration;

    public StopMachine(
            WebClientFacade webClientFacade,
            HostsConfiguration hostsConfiguration
    ) {
        this.webClientFacade = webClientFacade;
        this.hostsConfiguration = hostsConfiguration;
    }

    public boolean stop(List<CycleEvent> ce, Event event) {
        if (ce.stream().map(m -> m.getMessage().getEvent()).collect(Collectors.toList()).contains(event)) {
            webClientFacade.finish(
                    hostsConfiguration.getClient() + "/message",
                    Message.builder()
                            .event(event)
                            .eventType(EventType.ALL)
                            .build()
            );
            return true;
        }
        return false;
    }
}
