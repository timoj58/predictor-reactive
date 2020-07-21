package com.timmytime.predictoreventscraperreactive.service;

import com.timmytime.predictoreventscraperreactive.model.ScraperModel;

public interface MessageService {
    void send(String provider, String competition);
    void send(ScraperModel scraperModel);
}
