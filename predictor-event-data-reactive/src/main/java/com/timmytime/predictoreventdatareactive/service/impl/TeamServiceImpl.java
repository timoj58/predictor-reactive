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

        return findByLabelLike(
                specialCasesFactory.getSpecialCase(
                        label)
                        .orElse(
                                new SpecialCase(label)
                        ).getName(),
                competition);
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

    private Optional<Team> findByLabelLike(String label, String competition) {
        Optional<Team> team = findByLabelIgnoreCaseAndCountry(label, competition);
        if (label.contains(" ")) {

            if (team.isEmpty()) {
                StringBuilder regex = new StringBuilder();

                List<String> words = Arrays.asList(label.split(" "));
                int counter = 0;
                for (String word : words) {
                    regex.append(word + (counter < (words.size() - 1) ? ".*" : ""));
                    counter++;
                }

                Optional<Team> regexMatch1 = findByLabelRegexIgnoreCaseAndCountry(regex.toString(), competition);

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

                    return findByLabelRegexIgnoreCaseAndCountry(regex.toString(), competition);
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
                    findByLabelLikeIgnoreCaseAndCountry(label, competition);
        }
    }


    private Optional<Team> findByLabelRegexIgnoreCaseAndCountry(String regex, String competition){
        log.info("regex {}", regex);
        return teams.get(competition)
                .values()
                .stream()
                .filter(f -> f.getLabel().toLowerCase().matches(regex.toLowerCase()))
                .findFirst();
    };

    private Optional<Team> findByLabelIgnoreCaseAndCountry(String label, String competition){
        return teams.get(competition)
                .values()
                .stream()
                .filter(f -> f.getLabel().equalsIgnoreCase(label))
                .findFirst();
    }

    private Optional<Team> findByLabelLikeIgnoreCaseAndCountry(String label, String competition){
        return teams.get(competition)
                .values()
                .stream()
                .filter(f -> f.getLabel().toLowerCase().contains(label.toLowerCase()))
                .findFirst();
    }

}
