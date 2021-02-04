package com.timmytime.predictorteamsreactive.service.impl;

import com.timmytime.predictorteamsreactive.enumerator.CountryCompetitions;
import com.timmytime.predictorteamsreactive.model.Team;
import com.timmytime.predictorteamsreactive.service.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.util.*;

@Slf4j
@Service("teamService")
public class TeamServiceImpl implements TeamService {


    private final String dataHost;
    private final Map<String, Map<UUID, Team>> teams = new HashMap<>();

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
                Arrays.stream(CountryCompetitions.values())
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
        return new ArrayList<>(teams.get(country).values());
    }
}
