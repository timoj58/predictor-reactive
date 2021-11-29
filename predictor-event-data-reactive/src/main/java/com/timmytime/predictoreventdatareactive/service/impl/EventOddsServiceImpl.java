package com.timmytime.predictoreventdatareactive.service.impl;

import com.timmytime.predictoreventdatareactive.enumerator.Providers;
import com.timmytime.predictoreventdatareactive.model.EventOdds;
import com.timmytime.predictoreventdatareactive.repo.EventOddsRepo;
import com.timmytime.predictoreventdatareactive.response.Event;
import com.timmytime.predictoreventdatareactive.service.EventOddsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.function.Consumer;

@Slf4j
@Service("eventOddsService")
public class EventOddsServiceImpl implements EventOddsService {

    private final EventOddsRepo eventOddsRepo;
    private Consumer<EventOdds> receive;

    @Autowired
    public EventOddsServiceImpl(
            EventOddsRepo eventOddsRepo
    ) {
        this.eventOddsRepo = eventOddsRepo;

        Flux<EventOdds> events = Flux.create(sink ->
                EventOddsServiceImpl.this.receive = sink::next, FluxSink.OverflowStrategy.BUFFER);

        events.limitRate(1).subscribe(this::process);

    }

    @Override
    public void addToQueue(EventOdds eventOdds) {
        receive.accept(eventOdds);
    }

    private Mono<EventOdds> create(EventOdds eventOdds) {
        return eventOddsRepo.save(eventOdds);
    }

    @Override
    public Mono<Void> delete(Providers providers) {
        return eventOddsRepo.deleteByProviderAndEventDateBefore(providers.name(), LocalDate.now().atStartOfDay());
    }

    @Override
    public Flux<Event> getEvents(String competition) {
        return eventOddsRepo.findByCompetition(competition)
                .distinct(EventOdds::getTeams)
                .map(Event::new);
    }

    private void process(EventOdds eventOdds) {
        eventOdds.getMatchTeams().subscribe(
                matchTeams -> {
                    var homeTeam = matchTeams.getHome();
                    var awayTeam = matchTeams.getAway();

                    if (homeTeam.isPresent() && awayTeam.isPresent()) {
                        create(eventOdds.toBuilder()
                                .teams(Arrays.asList(
                                        homeTeam.get().getId(), awayTeam.get().getId()
                                ))
                                .build()).doOnError(e -> log.error("saving error", e)).subscribe();
                    } else {
                        log.error("one or more teams NOF");
                    }
                });
    }
}
