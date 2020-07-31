package com.timmytime.predictorplayersreactive.service.impl;

import com.timmytime.predictorplayersreactive.model.PlayerMatch;
import com.timmytime.predictorplayersreactive.request.PlayerEventOutcomeCsv;
import com.timmytime.predictorplayersreactive.service.TensorflowDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service("tensorflowService")
public class TensorflowDataServiceImpl implements TensorflowDataService {

    private static final Logger log = LoggerFactory.getLogger(TensorflowDataServiceImpl.class);

    private final List<PlayerMatch> playerMatches = new ArrayList<>();

    private final Flux<PlayerMatch> receiver;
    private Consumer<PlayerMatch> consumer;

    @Autowired
    public TensorflowDataServiceImpl(
    ){
        this.receiver
                = Flux.push(sink -> consumer = (t) -> sink.next(t), FluxSink.OverflowStrategy.DROP);

        this.receiver.subscribe(this::process);
    }


    private void process(PlayerMatch playerMatch){
        playerMatches.add(playerMatch);
    }

    @Override
    public void load(PlayerMatch match) {
        this.consumer.accept(match);
    }

    @Override
    public void clear() {
        playerMatches.clear();
    }

    @Override
    public List<PlayerEventOutcomeCsv> getPlayerCsv(String fromDate, String toDate) {
        LocalDate startDate = LocalDate.parse(fromDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        LocalDate endDate = LocalDate.parse(toDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        return playerMatches
                .stream()
                .filter(f -> f.getDate().toLocalDate().isEqual(startDate) || f.getDate().toLocalDate().isAfter(startDate))
                .filter(f -> f.getDate().toLocalDate().isBefore(endDate))
                .sorted(Comparator.comparing(PlayerMatch::getDate))
                .map(PlayerEventOutcomeCsv::new)
                .collect(Collectors.toList());
    }
}
