package com.timmytime.predictorclientreactive.service;

public interface ShutdownService {
    void receive(String service);
    void shutdown();
}
