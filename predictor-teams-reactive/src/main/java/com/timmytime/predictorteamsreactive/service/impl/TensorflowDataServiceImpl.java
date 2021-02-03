package com.timmytime.predictorteamsreactive.service.impl;

import com.timmytime.predictorteamsreactive.enumerator.CountryCompetitions;
import com.timmytime.predictorteamsreactive.model.CountryMatch;
import com.timmytime.predictorteamsreactive.model.Match;
import com.timmytime.predictorteamsreactive.response.CompetitionEventOutcomeCsv;
import com.timmytime.predictorteamsreactive.service.TensorflowDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Service("tensorflowService")
public class TensorflowDataServiceImpl implements TensorflowDataService {

    private final Map<String, List<Match>> data = new HashMap<>();
    private Consumer<CountryMatch> consumer;

    @Autowired
    public TensorflowDataServiceImpl(

    ) {

        Arrays.stream(
                CountryCompetitions.values()
        ).forEach(country -> data.put(country.name().toLowerCase(), new ArrayList<>()));

        Flux<CountryMatch> receiver = Flux.push(sink -> consumer = sink::next, FluxSink.OverflowStrategy.BUFFER);
        receiver.subscribe(this::process);
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


    private void process(CountryMatch countryMatch) {
        data.get(countryMatch.getCountry()).add(countryMatch.getMatch());
    }

}
