package com.timmytime.predictordatareactive.service.impl;

import com.timmytime.predictordatareactive.enumerator.CountryCompetitions;
import com.timmytime.predictordatareactive.model.Team;
import com.timmytime.predictordatareactive.repo.TeamRepo;
import com.timmytime.predictordatareactive.service.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service("teamService")
public class TeamServiceImpl implements TeamService {


    private final TeamRepo teamRepo;
    private final Map<String, Map<UUID, Team>> lookup = new HashMap<>();

    @Autowired
    public TeamServiceImpl(
            TeamRepo teamRepo) {

        this.teamRepo = teamRepo;
        loadLookup();

    }


    @Override
    public void updateCompetition(List<Team> teams, String competition) {
        teams.forEach(team -> {
            team.setCompetition(competition);
            teamRepo.save(team).subscribe();
        });

    }

    @Override
    public List<Team> getTeams(String country) {
        return new ArrayList<>(lookup.get(country).values());
    }

    @Override
    public List<Team> getTeamsByCompetition(String competition) {

        return lookup.values()
                .stream()
                .map(m -> new ArrayList<>(m.values()))
                .flatMap(List::stream)
                .filter(f -> f.getCompetition().equals(competition))
                .collect(Collectors.toList());

    }


    @Override
    public Team find(UUID id, String country) {
        return lookup.get(country).get(id);
    }


    @Override
    public Team save(Team match) {
        return null;
    }

    @Override
    public Optional<Team> getTeam(String country, String label, String espnId) {
        return
                lookup.get(country)
                        .values()
                        .stream()
                        .filter(f -> f.getLabel().equalsIgnoreCase(label)
                                || f.getEspnId().equalsIgnoreCase(espnId))
                        .findFirst();

    }


    @Override
    public Team createNewTeam(Team team) {
        var placeholder = lookup.get(team.getCountry()).values()
                .stream()
                .filter(f -> f.getCompetition().equals("TBC"))
                .findFirst().get();

        teamRepo.save(placeholder).subscribe();

        placeholder.setCompetition(team.getCompetition());
        placeholder.setLabel(team.getLabel());
        placeholder.setEspnId(team.getEspnId());

        lookup.get(team.getCountry()).put(placeholder.getId(), placeholder);

        return placeholder;
    }

    private void loadLookup() {
        this.teamRepo.findAll().groupBy(Team::getCountry)
                .flatMap(Flux::collectList)
                .subscribe(teamsByCountry -> {
                    Map<UUID, Team> teams = new HashMap<>();

                    teamsByCountry
                            .forEach(team -> teams.put(team.getId(), team));

                    lookup.put(
                            teamsByCountry
                                    .stream()
                                    .map(Team::getCountry)
                                    .distinct()
                                    .findFirst()
                                    .get()
                            , teams);
                });
    }

    @PostConstruct
    private void initDb() {
        teamRepo.findAll().count().filter(count -> count == 0).doOnNext(
                then -> Flux.fromArray(CountryCompetitions.values())
                        .subscribe(country ->
                                Flux.fromStream(
                                        CountryCompetitions.valueOf(country.name()).getCompetitions().stream()
                                ).subscribe(competition ->

                                    IntStream.range(0, 50).forEach(index -> teamRepo.save(
                                            Team.builder()
                                                    .country(country.name().toLowerCase())
                                                    .label("dummy")
                                                    .competition("TBC")
                                                    .espnId("dummy")
                                                    .id(UUID.randomUUID())
                                                    .build()
                                    ).subscribe())
                                )
                        )
        ).doFinally(finishLookup ->
                Mono.just(1).delayElement(Duration.ofSeconds(10)).subscribe(load -> loadLookup()))
        .subscribe();
    }
}
