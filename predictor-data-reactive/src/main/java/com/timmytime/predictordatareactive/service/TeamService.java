package com.timmytime.predictordatareactive.service;

import com.timmytime.predictordatareactive.model.Team;
import com.timmytime.predictordatareactive.response.MatchTeams;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamService {
    Team find(UUID id, String country);

    Team save(Team match);

    Optional<Team> getTeam(String country, String label, String espnId);

    void updateCompetition(List<Team> teams, String competition);

    List<Team> getTeams(String country);

    Flux<Team> getTeamsFlux(@PathVariable String country);

    List<Team> getTeamsByCompetition(@PathVariable String competition);

    Team createNewTeam(Team team);

    Mono<MatchTeams> getMatchTeams(
            @PathVariable String competition,
            @RequestParam String home,
            @RequestParam String away);

    void init();

}
