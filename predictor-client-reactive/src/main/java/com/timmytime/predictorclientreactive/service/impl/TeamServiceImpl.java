package com.timmytime.predictorclientreactive.service.impl;

import com.timmytime.predictorclientreactive.enumerator.CountryCompetitions;
import com.timmytime.predictorclientreactive.model.Team;
import com.timmytime.predictorclientreactive.service.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public void loadTeams() {

        Flux.fromStream(
                Stream.of(CountryCompetitions.values())
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

    @Override
    public List<Team> get(String country) {
        return teams.get(country).values().stream().collect(Collectors.toList());
    }

}
