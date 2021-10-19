package com.timmytime.predictorscraperreactive.service.impl;

import com.timmytime.predictorscraperreactive.factory.ScraperFactory;
import com.timmytime.predictorscraperreactive.service.MatchScraperService;
import com.timmytime.predictorscraperreactive.service.PageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class MatchScraperServiceImpl implements MatchScraperService {

    private final ScraperFactory scraperFactory;
    private final PageService pageService;

    @Autowired
    public MatchScraperServiceImpl(
            ScraperFactory scraperFactory,
            PageService pageService
    ) {
        this.scraperFactory = scraperFactory;
        this.pageService = pageService;
    }


    @Scheduled(fixedRateString = "${scheduler.retry}")
    private void retry() {
        Flux.fromStream(scraperFactory.getScraperTrackerService().getFailedPlayersRequests().stream())
                .subscribe(pageService::addPageRequest);
    }

}
