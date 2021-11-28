package com.timmytime.predictorteamsreactive.service.impl;

import com.timmytime.predictorteamsreactive.enumerator.CountryCompetitions;
import com.timmytime.predictorteamsreactive.facade.WebClientFacade;
import com.timmytime.predictorteamsreactive.model.CountryMatch;
import com.timmytime.predictorteamsreactive.model.Match;
import com.timmytime.predictorteamsreactive.response.CompetitionEventOutcomeCsv;
import com.timmytime.predictorteamsreactive.service.TensorflowDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Service("tensorflowService")
public class TensorflowDataServiceImpl implements TensorflowDataService {

    private final Map<String, List<Match>> data = new HashMap<>();
    private final WebClientFacade webClientFacade;
    private final String eventsHost;
    private final String dataHost;
    private Consumer<CountryMatch> consumer;

    @Autowired
    public TensorflowDataServiceImpl(
            @Value("${clients.events}") String eventsHost,
            @Value("${clients.data}") String dataHost,
            WebClientFacade webClientFacade
    ) {
        this.eventsHost = eventsHost;
        this.dataHost = dataHost;
        this.webClientFacade = webClientFacade;
        Flux<CountryMatch> receiver = Flux.create(sink -> consumer = sink::next, FluxSink.OverflowStrategy.BUFFER);
        receiver.subscribe(this::process);

        Arrays.asList(CountryCompetitions.values())
                .forEach(country -> data.put(country.name().toLowerCase(), new ArrayList<>()));

    }

    @Override
    public void load(CountryMatch match) {
        this.consumer.accept(match);
    }

    @Override
    public List<CompetitionEventOutcomeCsv> getCountryCsv(String country, String fromDate, String toDate) {

        LocalDate startDate = LocalDate.parse(fromDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        LocalDate endDate = LocalDate.parse(toDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        return data.get(country)
                .stream()
                .filter(f -> f.getDate().toLocalDate().isEqual(startDate) || f.getDate().toLocalDate().isAfter(startDate))
                .filter(f -> f.getDate().toLocalDate().isBefore(endDate))
                .sorted(Comparator.comparing(Match::getDate))
                .map(CompetitionEventOutcomeCsv::new)
                .collect(Collectors.toList());
    }

    @Override
    public void clear(String country) {
        if (data.containsKey(country)) {
            data.get(country).clear();
        } else {
            log.info("no data to clear for {}", country);
        }
    }

    @Override
    public void loadOutstanding(String country, Runnable finish) {
        log.info("loading outstanding games");
        webClientFacade.getOutstandingEvents(
                eventsHost + "/outstanding/" + country.toLowerCase()
        ).doOnNext(event -> {
                    log.info("found outstanding event {} vs {}, {}", event.getHome(), event.getAway(), event.getDate().toString());
                    webClientFacade.getMatch(dataHost + "/match" +
                            "?home=" + event.getHome() + "&away=" + event.getAway()
                            + "&date=" + event.getDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                            .subscribe(match -> {
                                        log.info("found the actual match {} vs {}", match.getHomeTeam(), match.getAwayTeam());
                                        load(new CountryMatch(
                                                country.toLowerCase(),
                                                match.toBuilder().date(LocalDateTime.now().minusDays(1)).build()) //need to ensure it gets picked up.
                                        );
                                    }

                            );
                }
        ).doFinally(then -> finish.run())
        .subscribe();
    }


    private void process(CountryMatch countryMatch) {
        data.get(countryMatch.getCountry()).add(countryMatch.getMatch());
    }

}
