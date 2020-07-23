package com.timmytime.predictordatareactive.service.impl;

import com.timmytime.predictordatareactive.model.Match;
import com.timmytime.predictordatareactive.model.Team;
import com.timmytime.predictordatareactive.repo.MatchRepo;
import com.timmytime.predictordatareactive.service.MatchService;
import com.timmytime.predictordatareactive.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("matchService")
public class MatchServiceImpl implements MatchService {

    private final MatchRepo matchRepo;
    private final TeamService teamService;

    @Autowired
    public MatchServiceImpl(
            MatchRepo matchRepo,
            TeamService teamService) {
        this.matchRepo = matchRepo;
        this.teamService = teamService;
    }

    @Override
    public Mono<Match> find(UUID uuid) {
        //always return from cache.
        return matchRepo.findById(uuid);
    }

    @Override
    public Mono<Match> save(Match match) {
        return matchRepo.save(match);
    }

    @Override
    public Mono<Match> find(UUID homeTeam, UUID awayTeam, LocalDateTime date) {
        return matchRepo.findByHomeTeamAndAwayTeamAndDate(homeTeam, awayTeam, date);
    }

    @Override
    public Mono<Match> getMatch(UUID home, UUID away, String date) {
        return matchRepo.findByHomeTeamAndAwayTeam(home,away)
                .filter(f -> f.getDate().toLocalDate().equals(LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"))))
                .next();
    }

    @Override
    public Flux<Match> getMatches(UUID team) {

        return Flux.concat(
                matchRepo.findByHomeTeam(team),
                matchRepo.findByAwayTeam(team)
        );
    }

    @Override
    public Flux<Match> getMatchesByCountry(String country, String fromDate, String toDate) {
        LocalDateTime start = LocalDate.parse(fromDate, DateTimeFormatter.ofPattern("dd-MM-yyyy")).atStartOfDay();
        LocalDateTime end = LocalDate.parse(toDate, DateTimeFormatter.ofPattern("dd-MM-yyyy")).atStartOfDay().plusDays(1);

        List<UUID> teams = teamService.getTeams(country).stream().map(Team::getId).collect(Collectors.toList());
        return Flux.concat(
                matchRepo.findByHomeTeamInAndDateBetween(teams, start, end),
                matchRepo.findByAwayTeamInAndDateBetween(teams, start, end)
        );
    }

    @Override
    public void delete(UUID id) {
        matchRepo.deleteById(id).subscribe();
    }



}