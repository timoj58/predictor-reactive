package com.timmytime.predictorscraperreactive.service.impl;

import com.timmytime.predictorscraperreactive.enumerator.CompetitionFixtureCodes;
import com.timmytime.predictorscraperreactive.enumerator.ScraperType;
import com.timmytime.predictorscraperreactive.facade.WebClientFacade;
import com.timmytime.predictorscraperreactive.factory.ScraperFactory;
import com.timmytime.predictorscraperreactive.service.MessageService;
import com.timmytime.predictorscraperreactive.service.PageService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.function.Consumer;

@Slf4j
@Service
public class PageServiceImpl implements PageService {

    private final RestTemplate restTemplate = new RestTemplate();
    @Getter
    private final ScraperFactory scraperFactory;
    private final MessageService messageService;
    private Consumer<Triple<CompetitionFixtureCodes, ScraperType, String>> requestConsumer;

    @Autowired
    public PageServiceImpl(
            ScraperFactory scraperFactory,
            MessageService messageService
    ) {
        this.scraperFactory = scraperFactory;
        this.messageService = messageService;

        Flux<Triple<CompetitionFixtureCodes, ScraperType, String>> requestQueue = Flux.push(sink ->
                PageServiceImpl.this.requestConsumer = sink::next, FluxSink.OverflowStrategy.BUFFER);

        requestQueue.limitRate(1).subscribe(this::process);
    }

    @Override
    public void addPageRequest(Triple<CompetitionFixtureCodes, ScraperType, String> request) {
        requestConsumer.accept(request);
    }

    private void process(Triple<CompetitionFixtureCodes, ScraperType, String> request) {
        scraperFactory.getScraperTrackerService().incrementRequest();

        var response = "";

        try {
            response = restTemplate.exchange(request.getRight(),
                    HttpMethod.GET, null, String.class).getBody();

            switch (request.getMiddle()) {
                case RESULTS:
                    scraperFactory.getScraperTrackerService().removeResultsFromQueue(request.getLeft());
                    Flux.fromStream(scraperFactory.getResultScraper().scrape(
                            request.getLeft(), response
                    ).stream())
                            .map(result -> Pair.of(request.getLeft(), messageService.send(result)))
                            .subscribe(matchId -> {
                                var matchRequest = scraperFactory.getPlayerScraper().createRequest(matchId);
                                scraperFactory.getScraperTrackerService().addMatch(Pair.of(matchRequest.getLeft(), matchRequest.getRight()));
                                addPageRequest(matchRequest);
                            });
                    break;
                case MATCH:
                    scraperFactory.getPlayerScraper().scrape(
                            request.getRight(), response
                    ).ifPresent(match -> {
                        scraperFactory.getScraperTrackerService().removeMatch(Pair.of(request.getLeft(), request.getRight()));
                        messageService.send(match);
                    });
                    break;
            }

        } catch (RestClientException restClientException) {
            switch (request.getMiddle()) {
                case RESULTS:
                    scraperFactory.getScraperTrackerService().addFailedResultsRequest(request);
                    break;
                case MATCH:
                    scraperFactory.getScraperTrackerService().addFailedPlayersRequest(request);
                    break;
            }
        }

    }
}
