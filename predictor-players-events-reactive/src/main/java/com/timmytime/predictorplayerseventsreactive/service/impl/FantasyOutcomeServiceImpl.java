package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayerseventsreactive.model.FantasyOutcome;
import com.timmytime.predictorplayerseventsreactive.repo.FantasyOutcomeRepo;
import com.timmytime.predictorplayerseventsreactive.service.FantasyOutcomeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service("fantasyOutcomeService")
public class FantasyOutcomeServiceImpl implements FantasyOutcomeService {

    private final FantasyOutcomeRepo fantasyOutcomeRepo;

    @Override
    public Mono<FantasyOutcome> save(FantasyOutcome fantasyOutcome) {
        return fantasyOutcomeRepo.save(fantasyOutcome);
    }

    @Override
    public Mono<FantasyOutcome> find(UUID id) {
        return fantasyOutcomeRepo.findById(id);
    }

    @Override
    public Flux<FantasyOutcome> findByPlayer(UUID id) {
        return fantasyOutcomeRepo.findByPlayerId(id);
    }

    @Override
    public Flux<FantasyOutcome> toFix() {
        return fantasyOutcomeRepo.findByPredictionNull();
    }

    @Override
    public Flux<FantasyOutcome> reset() {
        return fantasyOutcomeRepo.findByPredictionNotNullAndEventDateGreaterThan(LocalDate.now().atStartOfDay());
    }

    @Override
    public Flux<FantasyOutcome> topSelections(String market, Integer threshold) throws ResponseStatusException {

        return fantasyOutcomeRepo.findByCurrentAndFantasyEventType(Boolean.TRUE, FantasyEventTypes.valueOf(market))
                .filter(f -> f.getEventDate().isAfter(LocalDateTime.now().minusDays(5)))
                .filter(f -> thresholdCheck(average(convert(f.getPrediction())), threshold));
    }

    private JSONArray convert(String prediction) {
        //legacy stuff.
        try {
            return new JSONObject(prediction).getJSONArray("result");
        } catch (Exception e) {
            return new JSONArray(prediction);
        }
    }

    private Double average(JSONArray predictions) {
        Double total = 0.0;
        for (int i = 0; i < predictions.length(); i++) {
            if (!predictions.getJSONObject(i).getString("key").equals("0")) {
                total += predictions.getJSONObject(i).getDouble("score");
            }
        }
        return total;
    }

    private Boolean thresholdCheck(Double prediction, Integer threshold) {
        return prediction >= threshold;
    }


    @Override
    public void init() {
        log.info("reset current events");
        fantasyOutcomeRepo.findByCurrent(Boolean.TRUE)
                .filter(r -> r.getEventDate().isBefore(LocalDateTime.now()))
                .limitRate(5)
                .subscribe(fantasyOutcome ->
                        fantasyOutcomeRepo.save(fantasyOutcome.toBuilder().current(Boolean.FALSE).build()).subscribe());

    }


}
