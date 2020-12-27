package com.timmytime.predictorplayerseventsreactive.service;


import com.timmytime.predictorplayerseventsreactive.request.PlayerEventOutcomeCsv;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface TensorflowDataService {

    List<PlayerEventOutcomeCsv> getPlayerCsv(
            @PathVariable String fromDate,
            @PathVariable String toDate);
}
