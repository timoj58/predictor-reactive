package com.timmytime.predictoreventdatareactive.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.timmytime.predictoreventdatareactive.enumerator.Providers;
import org.apache.commons.lang3.tuple.Pair;

public interface ProviderService {
    void receive(Pair<Providers, JsonNode> message);

}
