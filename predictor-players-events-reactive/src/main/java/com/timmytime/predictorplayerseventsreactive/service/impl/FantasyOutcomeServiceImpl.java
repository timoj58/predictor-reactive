package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayerseventsreactive.model.FantasyOutcome;
import com.timmytime.predictorplayerseventsreactive.repo.FantasyOutcomeRepo;
import com.timmytime.predictorplayerseventsreactive.service.FantasyOutcomeService;
import com.timmytime.predictorplayerseventsreactive.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service("fantasyOutcomeService")
public class FantasyOutcomeServiceImpl implements FantasyOutcomeService {

    private final FantasyOutcomeRepo fantasyOutcomeRepo;
    private final PlayerService playerService;

    @Override
    public Mono<FantasyOutcome> save(FantasyOutcome fantasyOutcome) {
        return Mono.just(fantasyOutcome)
                .doOnNext(f -> fantasyOutcomeRepo.findByPlayerId(f.getPlayerId())
                        .filter(r -> r.getEventDate().isBefore(LocalDateTime.now()))
                        .doOnNext(r -> fantasyOutcomeRepo.save(
                                r.toBuilder().current(Boolean.FALSE).build()
                                ).subscribe()
                        )
                ).doFinally(save -> fantasyOutcomeRepo.save(fantasyOutcome));
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
    public Flux<FantasyOutcome> topSelections(String market, Integer threshold) {
        return fantasyOutcomeRepo.findByCurrentAndFantasyEventType(Boolean.TRUE, FantasyEventTypes.valueOf(market))
                .filter(f -> f.getEventDate().isAfter(LocalDateTime.now().minusDays(5)))
                .filter(f -> thresholdCheck(average(convert(f.getPrediction())),threshold))
                .map(m -> m.toBuilder().label(playerService.get(m.getPlayerId()).getLabel()).build());
    }

    private JSONArray convert(String prediction){
        //legacy stuff.
        try {
            return new JSONObject(prediction).getJSONArray("result");
        } catch (Exception e) {
            return new JSONArray(prediction);
        }
    }

    private Double average(JSONArray predictions){
        Double total = 0.0;
        for(int i=0;i<predictions.length();i++){
            if(!predictions.getJSONObject(i).getString("key").equals("0")) {
                total += predictions.getJSONObject(i).getDouble("score");
            }
        }
        return total;
    }

    private Boolean thresholdCheck(Double prediction, Integer threshold){
        return prediction >= threshold;
    }

    //@PostConstruct
    private void init() {
        //no longer validating for now, so simply turn them all off when rebooting system.
        //but not until its live.  need data for now ;)
        fantasyOutcomeRepo.findByCurrent(Boolean.FALSE)
                .filter(f -> f.getEventDate().isAfter(LocalDateTime.now().minusDays(3)))
                .limitRate(5)
                .subscribe(outcome -> {
                    outcome.setCurrent(Boolean.TRUE);
                    fantasyOutcomeRepo.save(outcome).subscribe();
                });
    }


}
