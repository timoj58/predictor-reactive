package com.timmytime.predictorplayersreactive.service.impl;

import com.timmytime.predictorplayersreactive.enumerator.ApplicableFantasyLeagues;
import com.timmytime.predictorplayersreactive.enumerator.CountryCompetitions;
import com.timmytime.predictorplayersreactive.model.Team;
import com.timmytime.predictorplayersreactive.service.TeamService;
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

    private static final Logger log = LoggerFactory.getLogger(TeamServiceImpl.class);

    private final String dataHost;

    private Map<UUID, Team> teams = new HashMap<>();


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
                Arrays.asList(ApplicableFantasyLeagues.values()).stream()
        ).subscribe(league -> {
                    WebClient.builder().build()
                            .get()
                            .uri(dataHost + "/teams/country/" + league.getCountry().toLowerCase())
                            .retrieve()
                            .bodyToFlux(Team.class)
                            .subscribe(team -> {
                                log.info("adding {}", team.getLabel());
                                teams.put(team.getId(), team);
                            });
                }
        );


    }

    @Override
    public Team getTeam( UUID id) {
        return teams.get(id);
    }


}
