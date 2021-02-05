package com.timmytime.predictordatareactive.factory;

import com.timmytime.predictordatareactive.model.Match;
import com.timmytime.predictordatareactive.model.Result;
import com.timmytime.predictordatareactive.model.ResultData;
import com.timmytime.predictordatareactive.model.Team;
import com.timmytime.predictordatareactive.service.MatchCreationService;
import com.timmytime.predictordatareactive.service.MatchRepairService;
import com.timmytime.predictordatareactive.service.MatchService;
import com.timmytime.predictordatareactive.service.TeamService;
import com.timmytime.predictordatareactive.util.TeamLabelMatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.parser.Parser;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

@RequiredArgsConstructor
@Slf4j
@Component
public class MatchFactory {

    //TEST THIS TODO
    public static UnaryOperator<String> format = data ->
            Parser.unescapeEntities(
                    StringEscapeUtils.escapeHtml4(data).replace("&nbsp;", "")
                    , Boolean.TRUE
            ).trim();
    private final TeamService teamService;
    private final MatchService matchService;
    private final MatchCreationService matchCreationService;
    private final MatchRepairService matchRepairService;

    public void createMatch(Result result) {
        log.info("creating match for {}", result.getMatchId());
        ResultData resultData = new ResultData(result);

        String homeTeamLabel = format.apply(resultData.getResult().getString("homeTeam"));
        String awayTeamLabel = format.apply(resultData.getResult().getString("awayTeam"));

        log.info("home " + homeTeamLabel + ", away " + awayTeamLabel);

        var countryTeams = teamService.getTeams(resultData.getResult().getString("country"));

        Team homeTeam = getOrCreate(homeTeamLabel, resultData, countryTeams);
        Team awayTeam = getOrCreate(awayTeamLabel, resultData, countryTeams);

        //so now can process as before...
        teamService.updateCompetition(Arrays.asList(homeTeam, awayTeam), resultData.getResult().getString("competition"));

        String eventDate = resultData.getResult().getString("date");
        LocalDateTime eventDateLdt = LocalDateTime.parse(eventDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm'Z'"));

        matchService.getMatch(homeTeam.getId(), awayTeam.getId(), eventDateLdt.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .switchIfEmpty(Mono.just(new Match()))
                .subscribe(match ->
                {
                    if (match.getId() == null) {
                        log.info("match nof, new event");
                        matchCreationService.create(
                                homeTeam,
                                awayTeam,
                                eventDateLdt,
                                resultData
                        );
                    } else {
                        log.info("a match is on file - repairing");
                        matchRepairService.repair(match);
                        matchCreationService.create(
                                homeTeam,
                                awayTeam,
                                eventDateLdt,
                                resultData
                        );

                    }
                });

    }

    private Team getOrCreate(String label, ResultData resultData, List<Team> countryTeams) {
        return teamService.getTeam(label, resultData.getResult().getString("country"))
                .or(() -> TeamLabelMatcher.match(label, countryTeams))
                .or(() -> Optional.of(teamService.createNewTeam(
                        Team.builder()
                                .country(resultData.getResult().getString("country"))
                                .label(label)
                                .competition(resultData.getResult().getString("competition")
                                ).build())
                        )
                ).get();
    }

}
