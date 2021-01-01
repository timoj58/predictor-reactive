package com.timmytime.predictorplayerseventsreactive.service;


import com.timmytime.predictorplayerseventsreactive.model.PlayerMatch;
import com.timmytime.predictorplayerseventsreactive.request.PlayerEventOutcomeCsv;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface TensorflowDataService {
    void load(PlayerMatch match);

    void delete();

    List<PlayerEventOutcomeCsv> getPlayerCsv(
            @PathVariable String fromDate,
            @PathVariable String toDate);
}
