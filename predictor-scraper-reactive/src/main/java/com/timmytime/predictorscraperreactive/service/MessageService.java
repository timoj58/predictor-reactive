package com.timmytime.predictorscraperreactive.service;

import com.timmytime.predictorscraperreactive.model.ScraperModel;
import com.timmytime.predictorscraperreactive.request.Message;

public interface MessageService {
    void send(ScraperModel scraperModel);
    void send(Message message);
    void send();
}
