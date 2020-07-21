package com.timmytime.predictoreventdatareactive.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface ProviderService {
    void receive(JsonNode message);
}
