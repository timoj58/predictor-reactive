package com.timmytime.predictordatareactive.service.impl;

import com.timmytime.predictordatareactive.model.Match;
import com.timmytime.predictordatareactive.model.ResultData;
import com.timmytime.predictordatareactive.model.Team;
import com.timmytime.predictordatareactive.repo.ResultRepo;
import com.timmytime.predictordatareactive.service.LineupPlayerService;
import com.timmytime.predictordatareactive.service.MatchCreationService;
import com.timmytime.predictordatareactive.service.MatchService;
import com.timmytime.predictordatareactive.service.StatMetricService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service("matchCreationService")
public class MatchCreationServiceImpl implements MatchCreationService {

    private final MatchService matchService;
    private final StatMetricService statMetricService;
    private final LineupPlayerService lineupPlayerService;
    private final ResultRepo resultRepo;

    @Override
    public void create(
            Team homeTeam,
            Team awayTeam,
            LocalDateTime date,
            ResultData resultData
    ) {

        Match match = new Match();
        match.setId(UUID.randomUUID());
        match.setDate(date);
        match.setHomeTeam(homeTeam.getId());
        match.setAwayTeam(awayTeam.getId());
        match.setHomeScore(resultData.getResult().getInt("homeScore"));
        match.setAwayScore(resultData.getResult().getInt("awayScore"));


        statMetricService.createTeamMetrics(
                match.getId(),
                homeTeam,
                "home",
                date,
                resultData).stream()
                .forEach(action -> action
                        .delayElement(Duration.ofMillis(100L))
                        .subscribe());

        statMetricService.createTeamMetrics(
                match.getId(),
                awayTeam,
                "away",
                date,
                resultData).stream()
                .forEach(action -> action
                        .delayElement(Duration.ofMillis(100L))
                        .subscribe());


        lineupPlayerService.processPlayers(
                resultData.getLineups().getJSONObject("data").getJSONArray("home"),
                homeTeam,
                match.getId(),
                date,
                resultData,
                "players");

        if (resultData.getLineups().getJSONObject("data").has("homePlayingSubs")) {
            lineupPlayerService.processPlayers(
                    resultData.getLineups().getJSONObject("data").getJSONArray("homePlayingSubs"),
                    homeTeam,
                    match.getId(),
                    date,
                    resultData,
                    "subs");
        }

        if (resultData.getLineups().getJSONObject("data").has("homeNonPlayingSubs")) {
            lineupPlayerService.processPlayers(
                    resultData.getLineups().getJSONObject("data").getJSONArray("homeNonPlayingSubs"),
                    homeTeam,
                    match.getId(),
                    date,
                    resultData,
                    "none");
        }

        lineupPlayerService.processPlayers(
                resultData.getLineups().getJSONObject("data").getJSONArray("away"),
                awayTeam,
                match.getId(),
                date,
                resultData,
                "players");

        if (resultData.getLineups().getJSONObject("data").has("awayPlayingSubs")) {
            lineupPlayerService.processPlayers(
                    resultData.getLineups().getJSONObject("data").getJSONArray("awayPlayingSubs"),
                    awayTeam,
                    match.getId(),
                    date,
                    resultData,
                    "subs");
        }

        if (resultData.getLineups().getJSONObject("data").has("awayNonPlayingSubs")) {
            lineupPlayerService.processPlayers(
                    resultData.getLineups().getJSONObject("data").getJSONArray("awayNonPlayingSubs"),
                    awayTeam,
                    match.getId(),
                    date,
                    resultData,
                    "none");
        }


        matchService.save(match).doAfterTerminate(() ->
                resultRepo.findById(resultData.getId()).subscribe(
                        result -> {
                            log.info("match {} processed", result.getMatchId());
                            result.setProcessed(Boolean.TRUE);
                            resultRepo.save(result).subscribe();
                        })
        )
                .subscribe();
    }


}
