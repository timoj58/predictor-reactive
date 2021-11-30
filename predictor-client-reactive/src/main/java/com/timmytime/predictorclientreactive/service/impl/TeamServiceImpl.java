package com.timmytime.predictorclientreactive.service.impl;

import com.timmytime.predictorclientreactive.enumerator.CountryCompetitions;
import com.timmytime.predictorclientreactive.model.Team;
import com.timmytime.predictorclientreactive.service.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

import static java.util.stream.Stream.of;
import static reactor.core.publisher.Flux.fromStream;

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
    public void load() {

        fromStream(
                of(CountryCompetitions.values())
        ).subscribe(country -> {
                    teams.put(country.name().toLowerCase(), new HashMap<>());
                    WebClient.builder().build()
                            .get()
                            .uri(dataHost + "/teams/country/" + country.name().toLowerCase())
                            .retrieve()
                            .bodyToFlux(Team.class)
                            .subscribe(team -> teams.get(country.name().toLowerCase()).put(team.getId(), team));
                }
        );


    }

    @Override
    public Team getTeam(String country, UUID id) {
        return teams.get(country).values().stream().filter(f -> f.getId().equals(id)).findFirst().get();
    }

    @Override
    public List<Team> get(String country) {
        return new ArrayList<>(teams.get(country).values());
    }

    @Override
    public Team getTeam(UUID id) {
        List<Team> allTeams = new ArrayList<>();

        teams.keySet().forEach(country -> allTeams.addAll(teams.get(country).values()));
        return allTeams.stream().filter(f -> f.getId().equals(id)).findFirst().get();
    }

}
