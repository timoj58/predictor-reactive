package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.model.PlayerMatch;
import com.timmytime.predictorplayerseventsreactive.repo.PlayerMatchRepo;
import com.timmytime.predictorplayerseventsreactive.request.PlayerEventOutcomeCsv;
import com.timmytime.predictorplayerseventsreactive.service.TensorflowDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Service("tensorflowService")
public class TensorflowDataServiceImpl implements TensorflowDataService {

    private final PlayerMatchRepo playerMatchRepo;
    private Consumer<PlayerMatch> consumer;

    @Autowired
    public TensorflowDataServiceImpl(
            PlayerMatchRepo playerMatchRepo
    ) {
        this.playerMatchRepo = playerMatchRepo;

        Flux<PlayerMatch> receiver = Flux.push(sink -> consumer = sink::next, FluxSink.OverflowStrategy.BUFFER);
        receiver.limitRate(1).subscribe(this::process);
    }


    private void process(PlayerMatch playerMatch) {
        playerMatchRepo.findByDateAndPlayerId(playerMatch.getDate(), playerMatch.getPlayerId())
                .ifPresentOrElse(then -> {}, () -> playerMatchRepo.save(playerMatch.toBuilder().id(UUID.randomUUID()).build()));
    }

    @Override
    public void load(PlayerMatch match) {
        this.consumer.accept(match);
    }

    @Override
    public List<PlayerEventOutcomeCsv> getPlayerCsv(String fromDate, String toDate) {
        LocalDate startDate = LocalDate.parse(fromDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        LocalDate endDate = LocalDate.parse(toDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        log.info("getting data {} {} ", fromDate, toDate);

        return playerMatchRepo
                .findByDateBetween(startDate, endDate)
                .stream()
                .map(PlayerEventOutcomeCsv::new)
                .collect(Collectors.toList());
    }
}
