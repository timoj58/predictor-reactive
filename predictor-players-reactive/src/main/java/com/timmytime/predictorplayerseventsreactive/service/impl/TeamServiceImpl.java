package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.enumerator.ApplicableFantasyLeagues;
import com.timmytime.predictorplayerseventsreactive.model.Team;
import com.timmytime.predictorplayerseventsreactive.service.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service("teamService")
public class TeamServiceImpl implements TeamService {

    private final String dataHost;
    private final Map<UUID, Team> teams = new HashMap<>();


    @Autowired
    public TeamServiceImpl(
            @Value("${clients.data}") String dataHost
    ) {
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
    public Team getTeam(UUID id) {
        return teams.get(id);
    }


}
