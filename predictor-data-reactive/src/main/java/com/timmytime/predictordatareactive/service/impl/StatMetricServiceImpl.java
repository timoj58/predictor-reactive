package com.timmytime.predictordatareactive.service.impl;

import com.timmytime.predictordatareactive.factory.MatchFactory;
import com.timmytime.predictordatareactive.model.Player;
import com.timmytime.predictordatareactive.model.ResultData;
import com.timmytime.predictordatareactive.model.StatMetric;
import com.timmytime.predictordatareactive.model.Team;
import com.timmytime.predictordatareactive.repo.StatMetricRepo;
import com.timmytime.predictordatareactive.service.StatMetricService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

@Service("scoreMetricService")
public class StatMetricServiceImpl implements StatMetricService {

    private final Logger log = LoggerFactory.getLogger(StatMetricServiceImpl.class);

    private final StatMetricRepo statMetricRepo;

    @Autowired
    public StatMetricServiceImpl(StatMetricRepo statMetricRepo) {
        this.statMetricRepo = statMetricRepo;
    }

    @Override
    public void delete(UUID id) {
        statMetricRepo.deleteById(id).subscribe();
    }

    @Override
    public Mono<StatMetric> save(StatMetric statMetric) {
        return statMetricRepo.save(statMetric);
    }

    @Override
    public List<Mono<StatMetric>> createTeamMetrics(
            UUID matchId,
            Team team,
            String label,
            LocalDateTime date,
            ResultData resultData
    ) {

        List<Mono<StatMetric>> stats = new ArrayList<>();

        JSONArray data = resultData.getMatch().getJSONArray("data");

        IntStream.range(0, data.length())
                .forEach(index -> {
                    log.info("processing stats {}", team.getLabel());

                   if(data.getJSONObject(index).getString("data-home-away").equals(label)) {

                       StatMetric statMetric = new StatMetric();
                       statMetric.setId(UUID.randomUUID());
                       statMetric.setTeam(team.getId());
                       statMetric.setTimestamp(date);
                       statMetric.setLabel(data.getJSONObject(index).getString("data-stat"));
                       statMetric.setValue(Integer.valueOf(data.getJSONObject(index).getString("value")));
                       statMetric.setMatchId(matchId);


                       log.info("adding a stat {}", statMetric.getLabel());
                       stats.add(
                               save(statMetric)
                       );
                   }
                });



        return stats;
    }

    @Override
    public List<Mono<StatMetric>> createPlayerMatchEventMetrics(
            UUID matchId,
            Player player,
            ResultData resultData,
            LocalDateTime date
    ) {
        List<Mono<StatMetric>> statMetrics = new ArrayList<>();
        JSONArray details = new JSONArray(resultData.getResult().getString("details"));

        IntStream.range(0, details.length()).forEach(i -> {

                    String escaped = details.getJSONObject(i).has("displayName") ?
                            Parser.unescapeEntities(MatchFactory.format.apply(details.getJSONObject(i).getString("displayName")), Boolean.TRUE)
                            : null;


                    if (escaped != null) {

                        if(player.getLabel().equalsIgnoreCase(
                              escaped.replace("  ", " "))
                        ){

                                StatMetric statMetric = new StatMetric();
                                statMetric.setId(UUID.randomUUID());
                                statMetric.setTimestamp(date);
                                statMetric.setLabel(details.getJSONObject(i).getString("text"));
                                statMetric.setTimeOfMetric(details.getJSONObject(i).getInt("value"));
                                statMetric.setPlayer(player.getId());  //need to fix this
                                statMetric.setMatchId(matchId);

                                statMetrics.add(
                                        save(statMetric)
                                );

                        }
                    } else {
                        log.info("we dont have a player for event " + details.getJSONObject(i).toString());
                    }
                }
        );

        return statMetrics;

    }

    @Override
    public List<Mono<StatMetric>> createPlayerIndividualEventMetrics(
            UUID matchId,
            Player player,
            LocalDateTime date
    ) {
        List<Mono<StatMetric>> statMetrics = new ArrayList<>();

                        player.getStats().stream().forEach(stat -> {

                                    String key = stat.keys().next();

                                    int value = Integer.valueOf(stat.getString(key));

                                    if(value > 0) {

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


}
