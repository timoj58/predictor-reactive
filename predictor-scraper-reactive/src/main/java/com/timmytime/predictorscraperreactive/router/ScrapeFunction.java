package com.timmytime.predictorscraperreactive.router;

import com.timmytime.predictorscraperreactive.service.ScraperService;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Component
public class ScrapeFunction {

    private final ScraperService scraperService;

    @Autowired
    public ScrapeFunction(
            ScraperService scraperService
    ) {
        this.scraperService = scraperService;
    }

    @Bean
    @RouterOperation(beanClass = ScraperService.class, beanMethod = "scrape")
    RouterFunction<ServerResponse> scrape() {

        return route(RequestPredicates.POST("/scrape"),
                (scrape) -> ServerResponse.ok().build(scraperService.scrape()));
    }

}
