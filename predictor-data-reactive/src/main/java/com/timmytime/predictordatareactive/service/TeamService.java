package com.timmytime.predictordatareactive.service;

import com.timmytime.predictordatareactive.model.Team;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamService {
    Team find(UUID id, String country);
    void delete(UUID id);
    Team save(Team match);
    Optional<Team> getTeam(@PathVariable String alias, @RequestParam  String country);
    void updateCompetition(List<Team> teams, String competition);
    List<Team> getTeams(@PathVariable String country);
    List<Team> getTeamsByCompetition(@PathVariable String competition);

}
