package com.timmytime.predictordatareactive.service.impl;

import com.timmytime.predictordatareactive.model.Match;
import com.timmytime.predictordatareactive.service.LineupPlayerService;
import com.timmytime.predictordatareactive.service.MatchRepairService;
import com.timmytime.predictordatareactive.service.MatchService;
import com.timmytime.predictordatareactive.service.StatMetricService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service("matchRepairService")
public class MatchRepairServiceImpl implements MatchRepairService {


    private final LineupPlayerService lineupPlayerService;
    private final StatMetricService statMetricService;
    private final MatchService matchService;

    @Override
    public void repair(Match match) {

        log.info("repairing match(es)");

        lineupPlayerService.deleteByMatch(match.getId())
                .doFinally(next -> {
                    statMetricService.deleteByMatch(match.getId()).subscribe();
                    matchService.delete(match.getId());
                })
                .subscribe();

    }

}
