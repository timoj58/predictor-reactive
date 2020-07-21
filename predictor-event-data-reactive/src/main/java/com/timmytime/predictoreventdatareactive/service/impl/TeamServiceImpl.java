package com.timmytime.predictoreventdatareactive.service.impl;

import com.timmytime.predictoreventdatareactive.configuration.SpecialCase;
import com.timmytime.predictoreventdatareactive.enumerator.CountryCompetitions;
import com.timmytime.predictoreventdatareactive.factory.SpecialCasesFactory;
import com.timmytime.predictoreventdatareactive.model.Team;
import com.timmytime.predictoreventdatareactive.service.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service("teamService")
public class TeamServiceImpl implements TeamService {

    private final Logger log = LoggerFactory.getLogger(TeamServiceImpl.class);

    private final SpecialCasesFactory specialCasesFactory;

    private Map<String, Map<UUID, Team>> teams = new HashMap<>();
    private final String dataHost;

    @Autowired
    public TeamServiceImpl(
            @Value("${data.host}") String dataHost,
            SpecialCasesFactory specialCasesFactory
    ){
        this.specialCasesFactory = specialCasesFactory;
        this.dataHost = dataHost;
    }

    @Override
    public Optional<Team> find(String label, String competition) {
        List<Team> teamsByCompetition = teams.get(competition).values().stream().collect(Collectors.toList());
        SpecialCase specialCase = specialCasesFactory.getSpecialCase(label).orElse(new SpecialCase(label));

        return teamsByCompetition
                .stream()
                .filter(f -> f.getLabel().equals(specialCase.getName()))
                .findFirst();
    }

    @Override
    @PostConstruct
    public void loadTeams() {

        Flux.fromStream(
                CountryCompetitions.getAllCompetitions().stream()

        ).subscribe(competition -> {
                    teams.put(competition, new HashMap<>());
                    WebClient.builder().build()
                            .get()
                            .uri(dataHost + "/teams/country/"
                                    + CountryCompetitions.findByCompetition(competition)
                                    .name()
                                    .toLowerCase()
                            )
                            .retrieve()
                            .bodyToFlux(Team.class)
                            .subscribe(team -> {
                                log.info("adding {}", team.getLabel());
                                teams.get(competition).put(team.getId(), team);
                            });
                }
        );

    }

}
