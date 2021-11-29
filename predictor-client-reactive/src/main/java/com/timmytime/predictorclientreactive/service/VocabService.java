package com.timmytime.predictorclientreactive.service;

import com.timmytime.predictorclientreactive.enumerator.CountryCompetitions;
import com.timmytime.predictorclientreactive.facade.S3Facade;
import com.timmytime.predictorclientreactive.facade.WebClientFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class VocabService {

    private final S3Facade s3Facade;
    private final WebClientFacade webClientFacade;
    private final TeamService teamService;

    private final String dataHost;
    private final Boolean players;
    private final Boolean teams;

    @Autowired
    public VocabService(
            @Value("${clients.data}") String dataHost,
            @Value("${vocab.players}") Boolean players,
            @Value("${vocab.teams}") Boolean teams,
            S3Facade s3Facade,
            WebClientFacade webClientFacade,
            TeamService teamService
    ) {
        this.dataHost = dataHost;
        this.players = players;
        this.teams = teams;
        this.s3Facade = s3Facade;
        this.webClientFacade = webClientFacade;
        this.teamService = teamService;
    }

    public Mono<Void> createVocab() {

        if (teams) {
            Map<String, StringBuilder> teamsVocab = new HashMap<>();
            StringBuilder allTeams = new StringBuilder();
            //teams
            Arrays.stream(CountryCompetitions.values())
                    .map(Enum::name)
                    .map(String::toLowerCase)
                    .map(teamService::get)
                    .forEach(teams -> {
                        var country = teams.stream().findFirst().get().getCountry();
                        teamsVocab.put(country, new StringBuilder());
                        teams.forEach(team -> {
                            teamsVocab.get(country).append(team.getId()).append("\n");
                            allTeams.append(team.getId()).append("\n");
                        });

                        s3Facade.put("predictor-team-models", "vocab/" + country + "/team-vocab.txt", teamsVocab.get(country).toString());
                    });


            s3Facade.put("predictor-player-models", "vocab/team-vocab.txt", allTeams.toString());
        }

        if (players) {
            //players (async fine)
            CompletableFuture.runAsync(() ->
                    webClientFacade.getFantasyPlayers(
                                    dataHost + "/players"
                            ).collectList()
                            .subscribe(players -> {
                                StringBuilder playerVocab = new StringBuilder();
                                players.forEach(player -> playerVocab.append(player.getId()).append("\n"));
                                s3Facade.put("predictor-player-models", "vocab/players-vocab.txt", playerVocab.toString());
                            })
            );
        }

        return Mono.empty();
    }
}
