package com.timmytime.predictorplayersreactive.service.impl;

import com.timmytime.predictorplayersreactive.facade.WebClientFacade;
import com.timmytime.predictorplayersreactive.model.PlayerMatch;
import com.timmytime.predictorplayersreactive.service.PlayerMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service("playerMatchService")
public class PlayerMatchServiceImpl implements PlayerMatchService {

    private final WebClientFacade webClientFacade;
    private final String dataHost;

    @Autowired
    public PlayerMatchServiceImpl(
            @Value("${data.host}") String dataHost,
            WebClientFacade webClientFacade
    ){
        this.dataHost = dataHost;
        this.webClientFacade = webClientFacade;
    }

    @Override
    public Flux<PlayerMatch> get(String fromDate, String toDate) {
        return null;
    }
}
