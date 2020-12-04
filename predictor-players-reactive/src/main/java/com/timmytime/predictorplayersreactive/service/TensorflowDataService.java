package com.timmytime.predictorplayersreactive.service;


import com.timmytime.predictorplayersreactive.model.PlayerMatch;
import com.timmytime.predictorplayersreactive.request.PlayerEventOutcomeCsv;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface TensorflowDataService {
    void load(PlayerMatch match);

    void clear();

    List<PlayerEventOutcomeCsv> getPlayerCsv(
            @PathVariable String fromDate,
            @PathVariable String toDate);
}
