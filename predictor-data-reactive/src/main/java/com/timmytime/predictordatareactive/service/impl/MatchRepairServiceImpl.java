package com.timmytime.predictordatareactive.service.impl;

import com.timmytime.predictordatareactive.model.Match;
import com.timmytime.predictordatareactive.model.ResultData;
import com.timmytime.predictordatareactive.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("matchRepairService")
public class MatchRepairServiceImpl implements MatchRepairService {

    private final Logger log = LoggerFactory.getLogger(MatchRepairServiceImpl.class);


    private final LineupPlayerService lineupPlayerService;
    private final LineupService lineupService;
    private final StatMetricService statMetricService;
    private final MatchService matchService;

    @Autowired
    public MatchRepairServiceImpl(
            LineupService lineupService,
            LineupPlayerService lineupPlayerService,
            StatMetricService statMetricService,
            MatchService matchService
    ){
        this.lineupService = lineupService;
        this.lineupPlayerService = lineupPlayerService;
        this.statMetricService = statMetricService;
        this.matchService = matchService;
    }

    @Override
    public void repair(Match match) {

        log.info("repairing match");

        lineupService.findByMatch(match.getId())
                .doOnNext(lineup -> lineupPlayerService.deleteByLineup(lineup.getId()).subscribe())
                .doFinally(next -> {
                    lineupService.deleteByMatch(match.getId()).subscribe();
                    statMetricService.deleteByMatch(match.getId()).subscribe();
                    matchService.delete(match.getId());
                });


    }
}
