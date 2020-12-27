package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.model.PlayerMatch;
import com.timmytime.predictorplayerseventsreactive.request.PlayerEventOutcomeCsv;
import com.timmytime.predictorplayerseventsreactive.service.TensorflowDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service("tensorflowService")
public class TensorflowDataServiceImpl implements TensorflowDataService {

    private final List<PlayerMatch> playerMatches = new ArrayList<>();


    @Autowired
    public TensorflowDataServiceImpl(
    ) {
    }


    @Override
    public List<PlayerEventOutcomeCsv> getPlayerCsv(String fromDate, String toDate) {
        LocalDate startDate = LocalDate.parse(fromDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        LocalDate endDate = LocalDate.parse(toDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        return playerMatches
                .stream()
                .filter(f -> f.getDate().toLocalDate().isEqual(startDate) || f.getDate().toLocalDate().isAfter(startDate))
                .filter(f -> f.getDate().toLocalDate().isBefore(endDate))
                //.sorted(Comparator.comparing(PlayerMatch::getDate))
                .map(PlayerEventOutcomeCsv::new)
                .collect(Collectors.toList());
    }
}
