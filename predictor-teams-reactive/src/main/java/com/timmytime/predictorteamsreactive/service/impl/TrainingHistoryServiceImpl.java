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
    public Boolean finished(Training type) {
        return trainingHistoryRepo.findByTypeAndCompletedFalse(type).isEmpty();
    }

    @Override
    public TrainingHistory find(Training type, String country) {
        return trainingHistoryRepo.findByTypeAndCountryOrderByDateDesc(type, country.toLowerCase()).stream().findFirst().get();
    }

    @Override
    public TrainingHistory clone(TrainingHistory trainingHistory) {

        TrainingHistory cloned = new TrainingHistory(
                trainingHistory.getType().equals(Training.TRAIN_RESULTS) ? Training.TRAIN_GOALS : Training.TRAIN_RESULTS,
                trainingHistory.getCountry(),
                trainingHistory.getFromDate(),
                trainingHistory.getToDate()
        );

        return trainingHistoryRepo.save(cloned);
    }

    @PostConstruct
    private void init() {
        if (trainingHistoryRepo.count() == 0) {
            log.info("initialize history");

            Arrays.asList(
                    CountryCompetitions.values()
            ).stream()
                    .forEach(country ->
                                Arrays.asList(Training.values())
                                        .stream()
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
