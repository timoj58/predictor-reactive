package com.timmytime.predictorclientreactive.service.impl;

import com.timmytime.predictorclientreactive.facade.WebClientFacade;
import com.timmytime.predictorclientreactive.model.Player;
import com.timmytime.predictorclientreactive.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PlayerServiceImpl implements PlayerService {

    private final Map<UUID, Player> players = new HashMap<>();
    private final WebClientFacade webClientFacade;
    @Value("${clients.data}")
    private String dataHost;

    @Override
    public void load() {

        webClientFacade.getPlayers(dataHost+"/players")
                .filter(f -> f.getLastAppearance() != null && f.getLastAppearance().isAfter(
                        LocalDate.now().minusYears(2)
                ))
                .subscribe(player -> players.put(player.getId(), player));

    }

    @Override
    public Player get(UUID id) {
        return players.get(id);
    }
}
