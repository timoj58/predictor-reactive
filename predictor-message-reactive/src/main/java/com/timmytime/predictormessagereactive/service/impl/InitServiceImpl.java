package com.timmytime.predictormessagereactive.service.impl;

import com.timmytime.predictormessagereactive.configuration.HostsConfiguration;
import com.timmytime.predictormessagereactive.facade.WebClientFacade;
import com.timmytime.predictormessagereactive.service.InitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Service
public class InitServiceImpl implements InitService {

    private final WebClientFacade webClientFacade;
    private final HostsConfiguration hostsConfiguration;

    private final Boolean testMode;

    @Autowired
    public InitServiceImpl(
            @Value("${test.mode}") Boolean testMode,
            HostsConfiguration hostsConfiguration,
            WebClientFacade webClientFacade
    ) {
        this.testMode = testMode;
        this.webClientFacade = webClientFacade;
        this.hostsConfiguration = hostsConfiguration;
    }

    @Override
    public Flux<String> init() {

        var url = new StringBuilder("/init");

        if (testMode) {
            var queryParam = LocalDate.now().minusDays(5).format(
                    DateTimeFormatter.ofPattern("dd-MM-yyyy")
            );
            url.append("?from=" + queryParam + "&to=" + queryParam);
        }

        return Flux.fromStream(hostsConfiguration.getInitHosts())
                .doOnNext(host -> webClientFacade.init(host + url));
    }
}
