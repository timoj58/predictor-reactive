package com.timmytime.predictorplayersreactive.service.impl;

import com.timmytime.predictorplayersreactive.enumerator.ApplicableFantasyLeagues;
import com.timmytime.predictorplayersreactive.facade.WebClientFacade;
import com.timmytime.predictorplayersreactive.model.Player;
import com.timmytime.predictorplayersreactive.model.PlayerResponse;
import com.timmytime.predictorplayersreactive.repo.PlayerResponseRepo;
import com.timmytime.predictorplayersreactive.service.PlayerResponseService;
import com.timmytime.predictorplayersreactive.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Service("playerResponseService")
public class PlayerResponseServiceImpl implements PlayerResponseService {

    private final PlayerResponseRepo playerResponseRepo;
    private final WebClientFacade webClientFacade;
    private final PlayerService playerService;

    @Autowired
    public PlayerResponseServiceImpl(
            WebClientFacade webClientFacade,
            PlayerService playerService,
            PlayerResponseRepo playerResponseRepo
    ){
        this.webClientFacade = webClientFacade;
        this.playerService = playerService;
        this.playerResponseRepo = playerResponseRepo;
    }

    @Override
    public Mono<PlayerResponse> getPlayer(UUID id) {
        return playerResponseRepo.findById(id);
    }

    @Override
    public void load(String country) {
        Flux.fromStream(
                ApplicableFantasyLeagues.findByCountry(country).stream()
        ).subscribe(league ->
                Flux.fromStream(
                        playerService.get(league.name().toLowerCase()).stream()
                )
                .doOnNext(player -> {})
                .doFinally(finish -> {}) //send a message to client system.  for league. (needs to be country but hey ho).
                .subscribe()
        );

    }

    @PostConstruct
    private void init(){
        playerResponseRepo.deleteAll(); //clear it out on start up.  can reload manually. TODO review.  for now its useful.
    }
}
