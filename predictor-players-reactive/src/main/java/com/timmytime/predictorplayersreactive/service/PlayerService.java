package com.timmytime.predictorplayersreactive.service;

import com.timmytime.predictorplayersreactive.model.Player;

import java.util.List;
import java.util.UUID;

public interface PlayerService {
    void load();
    List<Player> get(String competition);
    List<Player> get(String competition, UUID team);
    List<Player> get();
}
