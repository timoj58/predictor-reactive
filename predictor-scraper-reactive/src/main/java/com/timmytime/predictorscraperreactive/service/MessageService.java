package com.timmytime.predictorscraperreactive.service;

import com.timmytime.predictorscraperreactive.model.ScraperModel;

public interface MessageService {
    Integer send(ScraperModel scraperModel);

    void send();

}
