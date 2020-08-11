package com.timmytime.predictorclientreactive.service.impl;

import com.timmytime.predictorclientreactive.facade.WebClientFacade;
import com.timmytime.predictorclientreactive.model.Team;
import com.timmytime.predictorclientreactive.service.TeamService;
import com.timmytime.predictorclientreactive.util.CountryCompetitions;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Service("teamService")
public class TeamServiceImpl implements TeamService {

    private static final Logger log = LoggerFactory.getLogger(TeamServiceImpl.class);

    private final String dataHost;

    private Map<String, Map<UUID, Team>> teams = new HashMap<>();


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
    public Team getTeam(String country, UUID id) {
        return teams.get(country).values().stream().filter(f -> f.getId().equals(id)).findFirst().get();
    }
}
