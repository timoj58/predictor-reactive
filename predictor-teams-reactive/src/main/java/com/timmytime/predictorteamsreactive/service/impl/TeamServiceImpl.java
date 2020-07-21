package com.timmytime.predictorteamsreactive.service.impl;

import com.timmytime.predictorteamsreactive.enumerator.CountryCompetitions;
import com.timmytime.predictorteamsreactive.model.Team;
import com.timmytime.predictorteamsreactive.service.TeamService;
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

    /*
      not used currently
     */

    private static final Logger log = LoggerFactory.getLogger(TeamServiceImpl.class);

    private Map<String, Map<UUID, Team>> teams = new HashMap<>();

    private final String dataHost;

    @Autowired
    public TeamServiceImpl(
            @Value("${data.host}") String dataHost
    ){
        this.dataHost = dataHost;
    }

    @Override
    @PostConstruct
    public void loadTeams() {

        Flux.fromStream(
                Arrays.asList(CountryCompetitions.values()).stream()
        ).subscribe(country -> {
            teams.put(country.name().toLowerCase(), new HashMap<>());
            WebClient.builder().build()
                    .get()
                    .uri(dataHost + "/teams/country/" + country.name().toLowerCase())
                    .retrieve()
                    .bodyToFlux(Team.class)
                    .subscribe(team -> {
                        log.info("adding {}", team.getLabel());
                        teams.get(country.name().toLowerCase()).put(team.getId(), team);
                    });
                }
        );


    }

    @Override
    public List<Team> getTeams(String country) {
        return teams.get(country).values().stream().collect(Collectors.toList());
    }
}
