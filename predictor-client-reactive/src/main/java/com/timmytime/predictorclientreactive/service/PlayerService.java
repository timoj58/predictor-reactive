package com.timmytime.predictorclientreactive.service;

import com.timmytime.predictorclientreactive.model.Player;

import java.util.UUID;

public interface PlayerService {
    void load();

    Player get(UUID id);
}
