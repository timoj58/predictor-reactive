package com.timmytime.predictordatareactive.service.impl;

import com.timmytime.predictordatareactive.model.Match;
import com.timmytime.predictordatareactive.model.Team;
import com.timmytime.predictordatareactive.repo.MatchRepo;
import com.timmytime.predictordatareactive.service.MatchService;
import com.timmytime.predictordatareactive.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service("matchService")
public class MatchServiceImpl implements MatchService {

    private final MatchRepo matchRepo;
    private final TeamService teamService;


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
    public Mono<Match> getMatch(UUID home, UUID away, String date) {
        LocalDate filter = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        return matchRepo.findByHomeTeamAndAwayTeam(home, away)
                .filter(f -> f.getDate().toLocalDate().equals(filter))
                .next();
    }


    @Override
    public Mono<Match> getMatchByOpponent(UUID opponent, Boolean home, String date) {

        LocalDate filter = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        return home ?
                matchRepo.findByAwayTeam(opponent)
                        .filter(f -> f.getDate().toLocalDate().equals(filter))
                        .next()
                :
                matchRepo.findByHomeTeam(opponent)
                        .filter(f -> f.getDate().toLocalDate().equals(filter))
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
        ).distinct(); //required and test.  as returning duplicates
    }

    @Override
    public void delete(UUID id) {
        matchRepo.deleteById(id).subscribe();
    }


}