package com.timmytime.predictordatareactive.service.impl;

import com.timmytime.predictordatareactive.model.Player;
import com.timmytime.predictordatareactive.model.StatMetric;
import com.timmytime.predictordatareactive.repo.StatMetricRepo;
import com.timmytime.predictordatareactive.service.StatMetricService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service("scoreMetricService")
public class StatMetricServiceImpl implements StatMetricService {

    private final StatMetricRepo statMetricRepo;

    @Override
    public Mono<StatMetric> save(StatMetric statMetric) {
        return statMetricRepo.save(statMetric);
    }


    @Override
    public List<Mono<StatMetric>> create(
            UUID matchId,
            Player player,
            LocalDateTime date
    ) {
        List<Mono<StatMetric>> statMetrics = new ArrayList<>();

        player.getStats().forEach(stat -> {

            String key = stat.keys().next();

            int value = Integer.valueOf(stat.getString(key));

            if (value > 0) {

                StatMetric statMetric = new StatMetric();

                statMetric.setId(UUID.randomUUID());
                statMetric.setTimestamp(date);
                statMetric.setValue(value);
                statMetric.setLabel(key);
                statMetric.setPlayer(player.getId());
                statMetric.setMatchId(matchId);

                statMetrics.add(
                        save(statMetric)
                );

            }
        });


        return statMetrics;
    }

    @Override
    public Mono<Void> deleteByMatch(UUID matchId) {
        return statMetricRepo.deleteByMatchId(matchId);
    }

    @Override
    public Flux<StatMetric> find(UUID player, UUID match) {
        return statMetricRepo.findByPlayerAndMatchId(player, match);
    }

    @Override
    public Flux<StatMetric> getPlayerStats(UUID player) {
        return statMetricRepo.findByPlayer(player);
    }

}
