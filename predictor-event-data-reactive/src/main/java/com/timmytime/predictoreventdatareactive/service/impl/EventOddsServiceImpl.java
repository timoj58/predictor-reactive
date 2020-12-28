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
import java.util.function.Consumer;

@Slf4j
@Service("eventOddsService")
public class EventOddsServiceImpl implements EventOddsService {

    private final EventOddsRepo eventOddsRepo;
    private final Flux<EventOdds> events;
    private Consumer<EventOdds> receive;

    @Autowired
    public EventOddsServiceImpl(
            EventOddsRepo eventOddsRepo
    ) {
        this.eventOddsRepo = eventOddsRepo;

        this.events = Flux.push(sink ->
                EventOddsServiceImpl.this.receive = (t) -> sink.next(t), FluxSink.OverflowStrategy.BUFFER);

        this.events.limitRate(1).subscribe(this::process);

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
        create(eventOdds).doOnError(e -> log.error("saving error", e)).subscribe();
    }
}
