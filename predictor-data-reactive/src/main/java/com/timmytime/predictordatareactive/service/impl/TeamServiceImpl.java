package com.timmytime.predictordatareactive.service.impl;

import com.timmytime.predictordatareactive.configuration.DataConfig;
import com.timmytime.predictordatareactive.configuration.SpecialCase;
import com.timmytime.predictordatareactive.factory.SpecialCasesFactory;
import com.timmytime.predictordatareactive.model.Team;
import com.timmytime.predictordatareactive.repo.TeamRepo;
import com.timmytime.predictordatareactive.service.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("teamService")
public class TeamServiceImpl implements TeamService {


    private final DataConfig dataConfig;
    private final TeamRepo teamRepo;
    private final SpecialCasesFactory specialCasesFactory;
    private final Map<String, Map<UUID, Team>> lookup = new HashMap<>();

    @Autowired
    public TeamServiceImpl(
            DataConfig dataConfig,
            TeamRepo teamRepo,
            SpecialCasesFactory specialCasesFactory) {

        this.dataConfig = dataConfig;
        this.teamRepo = teamRepo;
        this.specialCasesFactory = specialCasesFactory;

        this.teamRepo.findAll()
                .groupBy(Team::getCountry)
                .flatMap(Flux::collectList)
                .subscribe(teamsByCountry -> {
                    Map<UUID, Team> teams = new HashMap<>();

                    teamsByCountry.stream()
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


    @Override
    public Optional<Team> getTeam(String alias, String country) {
        return findByLabelLike(
                specialCasesFactory.getSpecialCase(
                        alias)
                        .orElse(
                                new SpecialCase(alias)
                        ).getName(),
                country);
    }

    @Override
    public void updateCompetition(List<Team> teams, String competition) {
        teams.stream().forEach(team -> {
            team.setCompetition(competition);
            teamRepo.save(team).subscribe();
        });

    }

    @Override
    public List<Team> getTeams(String country) {
        return lookup.get(country).values().stream().collect(Collectors.toList());
    }

    @Override
    public List<Team> getTeamsByCompetition(String competition) {

        return lookup.values()
                .stream()
                .map(m -> m.values().stream().collect(Collectors.toList()))
                .flatMap(List::stream)
                .filter(f -> f.getCompetition().equals(competition))
                .collect(Collectors.toList());

    }


    private Optional<Team> findByLabelLike(String label, String country) {
        Optional<Team> team = findByLabelIgnoreCaseAndCountry(label, country);
        if (label.contains(" ")) {

            if (team.isEmpty()) {
                StringBuilder regex = new StringBuilder();

                List<String> words = Arrays.asList(label.split(" "));
                int counter = 0;
                for (String word : words) {
                    regex.append(word + (counter < (words.size() - 1) ? ".*" : ""));
                    counter++;
                }

                Optional<Team> regexMatch1 = findByLabelRegexIgnoreCaseAndCountry(regex.toString(), country);

                if (regexMatch1.isEmpty()) {
                    //regex each letter in the last word due to fucking abbreviations.  plus same other langs.
                    //and probably first word too. Atl. Madrid = Athletic
                    //but for now, as i havent seen all cases and can do it nicely.  just use first letter.
                    regex = new StringBuilder();

                    counter = 0;
                    for (String word : words) {
                        regex.append((counter < (word.length() - 1) ?
                                word : word.charAt(0) + ".*")
                                + (counter < (word.length() - 1) ? ".*" : ""));
                        counter++;
                    }

                    return findByLabelRegexIgnoreCaseAndCountry(regex.toString(), country);
                } else {
                    return regexMatch1;
                }

            }
            return team;
        } else {
            //direct match first.
            return team.isPresent() ?
                    team
                    :
                    findByLabelLikeIgnoreCaseAndCountry(label, country);
        }
    }


    @Override
    public Team find(UUID id, String country) {
        return lookup.get(country).get(id);
    }

    @Override
    public void delete(UUID id) {

    }

    @Override
    public Team save(Team match) {
        return null;
    }

    private Optional<Team> findByLabelRegexIgnoreCaseAndCountry(String regex, String country) {
        log.info("regex {}", regex);
        return lookup.get(country)
                .values()
                .stream()
                .filter(f -> f.getLabel().toLowerCase().matches(regex.toLowerCase()))
                .findFirst();
    }

    private Optional<Team> findByLabelIgnoreCaseAndCountry(String label, String country) {
        return lookup.get(country)
                .values()
                .stream()
                .filter(f -> f.getLabel().equalsIgnoreCase(label))
                .findFirst();
    }

    private Optional<Team> findByLabelLikeIgnoreCaseAndCountry(String label, String country) {
        return lookup.get(country)
                .values()
                .stream()
                .filter(f -> f.getLabel().toLowerCase().contains(label.toLowerCase()))
                .findFirst();
    }

    public Mono<Void> loadNewTeams() {
        return Flux.fromStream(dataConfig.getCountries().stream())
                .doOnNext(country ->
                        country.getCompetitions()
                                .stream()
                                .forEach(competition -> {
                                    log.info("processing {}", competition.getCompetition());
                                    Flux.fromArray(competition.getTeams().split(","))
                                            .subscribe(team -> teamRepo.findByLabelIgnoreCase(team)
                                                    .switchIfEmpty(Mono.just(
                                                            Team.builder()
                                                                    .label(team)
                                                                    .competition(competition.getCompetition())
                                                                    .country(country.getCountry()).build()))
                                                    .filter(missing -> missing.getId() == null)
                                                    .subscribe(save -> {
                                                        log.info("adding new team {}", save.getLabel());
                                                        teamRepo.save(save.toBuilder().id(UUID.randomUUID()).build()).subscribe();
                                                    }));
                                } )
                ).then(Mono.empty());
    }
}
