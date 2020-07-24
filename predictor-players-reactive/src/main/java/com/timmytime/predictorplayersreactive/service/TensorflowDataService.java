package com.timmytime.predictorplayersreactive.service;


import com.timmytime.predictorplayersreactive.model.PlayerMatch;
import com.timmytime.predictorplayersreactive.request.PlayerEventOutcomeCsv;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface TensorflowDataService {
    void load(PlayerMatch match);
    List<PlayerEventOutcomeCsv> getPlayerCsv(
            @RequestParam String fromDate,
            @RequestParam String toDate);
}
