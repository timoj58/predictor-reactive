package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayerseventsreactive.model.PlayersTrainingHistory;
import com.timmytime.predictorplayerseventsreactive.repo.PlayersTrainingHistoryRepo;
import com.timmytime.predictorplayerseventsreactive.service.PlayersTrainingHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service("playersTrainingHistoryService")
public class PlayersTrainingHistoryServiceImpl implements PlayersTrainingHistoryService {

    private final PlayersTrainingHistoryRepo playersTrainingHistoryRepo;

    @Override
    public Mono<PlayersTrainingHistory> find(UUID id) {
        return Mono.just(playersTrainingHistoryRepo.findById(id).get());
    }

    @Override
    public Mono<PlayersTrainingHistory> save(PlayersTrainingHistory trainingHistory) {
        return Mono.just(playersTrainingHistoryRepo.save(trainingHistory));
    }

    @Override
    public void saveNormal(PlayersTrainingHistory trainingHistory) {
        playersTrainingHistoryRepo.save(trainingHistory);
    }

    @Override
    public Mono<PlayersTrainingHistory> find(FantasyEventTypes type) {
        return Mono.just(playersTrainingHistoryRepo.findFirstByTypeOrderByDateDesc(type));
    }

    @Override
    public Optional<PlayersTrainingHistory> findOptional(FantasyEventTypes type) {
        return Optional.ofNullable(playersTrainingHistoryRepo.findFirstByTypeOrderByDateDesc(type));
    }

    @Override
    public void init() {

        Arrays.stream(FantasyEventTypes.values())
                .filter(f -> f.getPredict() == Boolean.TRUE)
                .forEach(type ->
                        findOptional(type)
                                .ifPresentOrElse(then -> log.info("we have history"),
                                        () -> {
                                            log.info("init record");
                                            var history = new PlayersTrainingHistory(
                                                    type,
                                                    LocalDate.parse("01-08-2009", DateTimeFormatter.ofPattern("dd-MM-yyyy")).atStartOfDay(),
                                                    LocalDate.parse("01-08-2009", DateTimeFormatter.ofPattern("dd-MM-yyyy")).atStartOfDay()
                                            );

                                            history.setCompleted(Boolean.TRUE);
                                            saveNormal(history);

                                        })

                );

    }

}
