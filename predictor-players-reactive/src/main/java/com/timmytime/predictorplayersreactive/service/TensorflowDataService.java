package com.timmytime.predictorplayersreactive.service;


import com.timmytime.predictorplayersreactive.model.PlayerMatch;
import com.timmytime.predictorplayersreactive.request.PlayerEventOutcomeCsv;

import java.util.List;

public interface TensorflowDataService {
    void load(PlayerMatch match);
    List<PlayerEventOutcomeCsv> getPlayerCsv(String fromDate, String toDate);
}
