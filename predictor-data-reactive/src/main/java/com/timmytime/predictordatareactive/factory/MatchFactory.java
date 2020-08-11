package com.timmytime.predictordatareactive.factory;

import com.timmytime.predictordatareactive.model.Match;
import com.timmytime.predictordatareactive.model.Result;
import com.timmytime.predictordatareactive.model.ResultData;
import com.timmytime.predictordatareactive.model.Team;
import com.timmytime.predictordatareactive.service.MatchCreationService;
import com.timmytime.predictordatareactive.service.MatchRepairService;
import com.timmytime.predictordatareactive.service.MatchService;
import com.timmytime.predictordatareactive.service.TeamService;
import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONObject;
import org.jsoup.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.UnaryOperator;

@Component
public class MatchFactory {

    private final Logger log = LoggerFactory.getLogger(MatchFactory.class);

    private final TeamService teamService;
    private final MatchService matchService;
    private final MatchCreationService matchCreationService;
    private final MatchRepairService matchRepairService;

    //TEST THIS TODO
    public static UnaryOperator<String> format = data ->
            Parser.unescapeEntities(
                    StringEscapeUtils.escapeHtml4(data).replace("&nbsp;", "")
                    , Boolean.TRUE
            ).trim();

    @Autowired
    public MatchFactory(
            TeamService teamService,
            MatchService matchService,
            MatchCreationService matchCreationService,
            MatchRepairService matchRepairService
    ){
        this.teamService = teamService;
        this.matchService = matchService;
        this.matchCreationService = matchCreationService;
        this.matchRepairService = matchRepairService;
    }

    public void createMatch(Result result) {
        log.info("creating match for {}", result.getMatchId());
        ResultData resultData = new ResultData(result);

        String homeTeamLabel = format.apply(resultData.getResult().getString("homeTeam"));
        String awayTeamLabel = format.apply(resultData.getResult().getString("awayTeam"));

        log.info("home " + homeTeamLabel + ", away " + awayTeamLabel);

        Optional<Team> homeTeam = teamService.getTeam(homeTeamLabel, resultData.getResult().getString("country"));
        Optional<Team> awayTeam = teamService.getTeam(awayTeamLabel, resultData.getResult().getString("country"));

        if (homeTeam.isPresent() && awayTeam.isPresent()){
            //so now can process as before...
            teamService.updateCompetition(Arrays.asList(homeTeam.get(), awayTeam.get()), resultData.getResult().getString("competition"));

            String eventDate = resultData.getResult().getString("date");
            LocalDateTime eventDateLdt = LocalDateTime.parse(eventDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm'Z'"));

            matchService.getMatch(homeTeam.get().getId(), awayTeam.get().getId(), eventDateLdt.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                    .switchIfEmpty(Mono.just(new Match()))
                    .subscribe(match ->
                    {
                        if(match.getId() == null){
                            log.info("match nof, new event");
                            matchCreationService.create(
                                    homeTeam.get(),
                                    awayTeam.get(),
                                    eventDateLdt,
                                    resultData
                            );
                        }else{
                            log.info("a match is on file - repairing");
                            matchRepairService.repair(match);
                            matchCreationService.create(
                                    homeTeam.get(),
                                    awayTeam.get(),
                                    eventDateLdt,
                                    resultData
                            );

                        }
                    });


        } else {
            log.info("one or both teams are not in teams list");
        }
    }
}
