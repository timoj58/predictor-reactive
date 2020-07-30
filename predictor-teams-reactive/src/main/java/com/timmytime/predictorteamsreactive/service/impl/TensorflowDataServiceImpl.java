package com.timmytime.predictorteamsreactive.service.impl;

import com.timmytime.predictorteamsreactive.enumerator.CountryCompetitions;
import com.timmytime.predictorteamsreactive.model.CountryMatch;
import com.timmytime.predictorteamsreactive.model.Match;
import com.timmytime.predictorteamsreactive.response.CompetitionEventOutcomeCsv;
import com.timmytime.predictorteamsreactive.service.TensorflowDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service("tensorflowService")
public class TensorflowDataServiceImpl implements TensorflowDataService {

    private static final Logger log = LoggerFactory.getLogger(TensorflowDataServiceImpl.class);

    private final Map<String, List<Match>> data = new HashMap<>();

    private final Flux<CountryMatch> receiver;
    private Consumer<CountryMatch> consumer;

    @Autowired
    public TensorflowDataServiceImpl(

    ){

        Arrays.asList(
                CountryCompetitions.values()
        ).stream()
                .forEach(country -> data.put(country.name().toLowerCase(), new ArrayList<>()));

        this.receiver
                = Flux.push(sink -> consumer = (t) -> sink.next(t), FluxSink.OverflowStrategy.DROP);

        this.receiver.subscribe(this::process);


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
        data.get(country).clear();

    }


    private void process(CountryMatch countryMatch){
        log.info("adding {} vs {} to {}", countryMatch.getMatch().getHomeTeam(), countryMatch.getMatch().getAwayTeam(), countryMatch.getCountry());
        data.get(countryMatch.getCountry()).add(countryMatch.getMatch());
    }

}
