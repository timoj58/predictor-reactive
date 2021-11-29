package com.timmytime.predictordatareactive.service.impl;

import com.timmytime.predictordatareactive.model.Match;
import com.timmytime.predictordatareactive.model.ResultData;
import com.timmytime.predictordatareactive.model.Team;
import com.timmytime.predictordatareactive.repo.ResultRepo;
import com.timmytime.predictordatareactive.service.LineupPlayerService;
import com.timmytime.predictordatareactive.service.MatchCreationService;
import com.timmytime.predictordatareactive.service.MatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service("matchCreationService")
public class MatchCreationServiceImpl implements MatchCreationService {

    private final MatchService matchService;
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


        lineupPlayerService.processPlayers(
                resultData.getLineups().getJSONObject("data").getJSONArray("home"),
                homeTeam,
                match.getId(),
                date);


        lineupPlayerService.processPlayers(
                resultData.getLineups().getJSONObject("data").getJSONArray("away"),
                awayTeam,
                match.getId(),
                date);


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
