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
    private final StatMetricService statMetricService;
    private final MatchService matchService;

    @Autowired
    public MatchRepairServiceImpl(
            LineupPlayerService lineupPlayerService,
            StatMetricService statMetricService,
            MatchService matchService
    ){
        this.lineupPlayerService = lineupPlayerService;
        this.statMetricService = statMetricService;
        this.matchService = matchService;
    }

    @Override
    public void repair(Match match) {

        log.info("repairing match");

        lineupPlayerService.deleteByMatch(match.getId())
                .doFinally(next -> {
                    statMetricService.deleteByMatch(match.getId()).subscribe();
                    matchService.delete(match.getId());
                });


    }
}
