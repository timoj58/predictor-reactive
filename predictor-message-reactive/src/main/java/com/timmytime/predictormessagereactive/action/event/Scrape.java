package com.timmytime.predictormessagereactive.action.event;

import com.timmytime.predictormessagereactive.action.EventAction;
import com.timmytime.predictormessagereactive.configuration.HostsConfiguration;
import com.timmytime.predictormessagereactive.enumerator.Action;
import com.timmytime.predictormessagereactive.enumerator.Event;
import com.timmytime.predictormessagereactive.facade.WebClientFacade;
import com.timmytime.predictormessagereactive.service.InitService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.stream.Stream;

@Component
public class Scrape implements IEventAction {

    private final Integer delay;
    private final WebClientFacade webClientFacade;
    private final HostsConfiguration hostsConfiguration;
    private final InitService initService;

    @Autowired
    public Scrape(
            @Value("${test.delay}") Integer delay,
            WebClientFacade webClientFacade,
            HostsConfiguration hostsConfiguration,
            InitService initService
    ) {
        this.delay = delay;
        this.webClientFacade = webClientFacade;
        this.hostsConfiguration = hostsConfiguration;
        this.initService = initService;
    }

    @Override
    public Pair<Action, EventAction> create() {
        return Pair.of(
                Action.SCRAPE,
                EventAction.builder()
                        .processed(Boolean.FALSE)
                        .handler((ce, ae) -> {
                            if (ce.stream().anyMatch(m -> m.getMessage().getEvent().equals(Event.START))) {
                                initService.init().doFinally(scrape ->
                                        Flux.fromStream(
                                                        Stream.of(hostsConfiguration.getDataScraper() + "/scrape", hostsConfiguration.getEventsScraper() + "/scrape")
                                                ).delayElements(Duration.ofSeconds(delay))
                                                .subscribe(webClientFacade::scrape)
                                ).subscribe();
                                return true;
                            }
                            return false;
                        })
                        .build()
        );
    }
}
