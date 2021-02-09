package com.timmytime.predictoreventsreactive.service.impl;

import com.timmytime.predictoreventsreactive.enumerator.CountryCompetitions;
import com.timmytime.predictoreventsreactive.enumerator.Predictions;
import com.timmytime.predictoreventsreactive.model.EventOutcome;
import com.timmytime.predictoreventsreactive.repo.EventOutcomeRepo;
import com.timmytime.predictoreventsreactive.service.EventOutcomeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service("eventOutcomeService")
public class EventOutcomeServiceImpl implements EventOutcomeService {

    private final EventOutcomeRepo eventOutcomeRepo;

    @Override
    public Mono<EventOutcome> save(EventOutcome eventOutcome) {
        return eventOutcomeRepo.save(eventOutcome);
    }

    @Override
    public Mono<EventOutcome> find(UUID id) {
        return eventOutcomeRepo.findById(id);
    }

    @Override
    public Flux<EventOutcome> toValidate(String country) {
        return eventOutcomeRepo.findByCompetitionInAndSuccessNull(
                CountryCompetitions.valueOf(country.toUpperCase()).getCompetitions()
        );
    }

    @Override
    public Flux<EventOutcome> lastEvents(String country) {
        return eventOutcomeRepo.findByCompetitionInAndPreviousEventTrue(
                CountryCompetitions.valueOf(country.toUpperCase()).getCompetitions()
        );
    }

    @Override
    public Flux<EventOutcome> previousEvents(String competition) {
        return eventOutcomeRepo.findByCompetitionInAndPreviousEventTrue(
                Arrays.asList(competition)
        );
    }

    @Override
    public Flux<EventOutcome> currentEvents(String competition) {
        return eventOutcomeRepo.findByCompetitionInAndSuccessNull(
                Arrays.asList(competition)
        );
    }

    @Override
    public Flux<EventOutcome> previousEventsByTeam(UUID team) {
        return Flux.concat(
                eventOutcomeRepo.findByHomeAndSuccessNotNullOrderByDateDesc(team),
                eventOutcomeRepo.findByAwayAndSuccessNotNullOrderByDateDesc(team)
        ).sort(Comparator.comparing(EventOutcome::getDate)
                .reversed()
        ).take(6);
    }

    @Override
    public Flux<EventOutcome> toFix() {
        return eventOutcomeRepo.findByPredictionNull();
    }

    @Override
    public Flux<EventOutcome> outstandingEvents(String country) {
        var minusDays = LocalDateTime.now().getDayOfWeek().equals(DayOfWeek.FRIDAY) ? 3 : 4;
        return eventOutcomeRepo.findByEventTypeAndCompetitionInAndSuccessNull(
                Predictions.PREDICT_RESULTS.name(), CountryCompetitions.valueOf(country.toUpperCase()).getCompetitions())
                .filter(f -> f.getDate().isAfter(LocalDateTime.now().minusDays(10))) //could make it dynamic.
                .filter(f -> f.getDate().isBefore(LocalDateTime.now().minusDays(minusDays)));
    }

    @Override
    public Flux<EventOutcome> topSelections(String outcome, Integer threshold) {
        return eventOutcomeRepo.findBySuccessNullAndEventType(Predictions.PREDICT_RESULTS.name())
                .filter(f -> f.getDate().isAfter(LocalDateTime.now().minusDays(5)))
                .filter(f -> thresholdCheck(filter(convert(f.getPrediction()), outcome), threshold));
    }

    private JSONArray convert(String prediction){
    //legacy stuff.
        try {
        return new JSONObject(prediction).getJSONArray("result");
    } catch (Exception e) {
            return new JSONArray(prediction);
        }
    }

    private JSONObject filter(JSONArray predictions, String outcome){
        for(int i=0;i<predictions.length();i++){
            if(predictions.getJSONObject(i).get("key").equals(outcome)){
                return predictions.getJSONObject(i);
            }
        }
        return new JSONObject().put("score", "0.0");
    }

    private Boolean thresholdCheck(JSONObject prediction, Integer threshold){
        return prediction.getDouble("score") >= threshold;
    }
}
