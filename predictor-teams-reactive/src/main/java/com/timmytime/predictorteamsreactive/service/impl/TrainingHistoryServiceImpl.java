package com.timmytime.predictorteamsreactive.service.impl;

import com.timmytime.predictorteamsreactive.enumerator.CountryCompetitions;
import com.timmytime.predictorteamsreactive.enumerator.Training;
import com.timmytime.predictorteamsreactive.model.TrainingHistory;
import com.timmytime.predictorteamsreactive.repo.TrainingHistoryRepo;
import com.timmytime.predictorteamsreactive.service.TrainingHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service("trainingHistoryService")
public class TrainingHistoryServiceImpl implements TrainingHistoryService {

    private final TrainingHistoryRepo trainingHistoryRepo;

    @Override
    public TrainingHistory find(UUID id) {
        return trainingHistoryRepo.findById(id).get();
    }

    @Override
    public TrainingHistory save(TrainingHistory trainingHistory) {
        return trainingHistoryRepo.save(trainingHistory);
    }

    @Override
    public TrainingHistory find(Training type, String country) {
        return trainingHistoryRepo.findByTypeAndCountryOrderByDateDesc(type, country.toLowerCase()).stream().findFirst().get();
    }

    @Override
    public TrainingHistory next(Training type, String country, Integer interval) {
        TrainingHistory previous = find(type, country.toLowerCase());
        return save(
                new TrainingHistory(
                        type,
                        country.toLowerCase(),
                        previous.getToDate(),
                        previous.getToDate().plusYears(interval).isAfter(LocalDateTime.now()) ?
                                LocalDateTime.now() :
                                previous.getToDate().plusYears(interval)
                )
        );
    }

    @Override
    public void completeTraining(TrainingHistory trainingHistory) {
        log.info("we have completed {} - {}", trainingHistory.getCountry(), trainingHistory.getType());
        trainingHistory.setCompleted(Boolean.TRUE);
        save(trainingHistory);
    }

    @Override
    public void init() {
        if (trainingHistoryRepo.count() == 0) {
            log.info("initialize history");

            Arrays.stream(
                    CountryCompetitions.values()
            )
                    .forEach(country ->
                            Arrays.stream(Training.values())
                                    .forEach(type -> {
                                        TrainingHistory trainingHistory = new TrainingHistory(
                                                type,
                                                country.name().toLowerCase(),
                                                LocalDate.parse("01-08-2009", DateTimeFormatter.ofPattern("dd-MM-yyyy")).atStartOfDay(),
                                                LocalDate.parse("01-08-2009", DateTimeFormatter.ofPattern("dd-MM-yyyy")).atStartOfDay());

                                        trainingHistory.setCompleted(Boolean.TRUE);
                                        trainingHistoryRepo.save(trainingHistory);
                                    })
                    );


        }
    }
}
