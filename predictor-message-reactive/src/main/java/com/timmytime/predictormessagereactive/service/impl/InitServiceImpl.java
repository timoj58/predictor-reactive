package com.timmytime.predictormessagereactive.service.impl;

import com.timmytime.predictormessagereactive.configuration.HostsConfiguration;
import com.timmytime.predictormessagereactive.facade.WebClientFacade;
import com.timmytime.predictormessagereactive.service.InitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;


@Service
public class InitServiceImpl implements InitService {

    private final WebClientFacade webClientFacade;
    private final HostsConfiguration hostsConfiguration;

    @Autowired
    public InitServiceImpl(
            HostsConfiguration hostsConfiguration,
            WebClientFacade webClientFacade
    ){
        this.webClientFacade = webClientFacade;
        this.hostsConfiguration = hostsConfiguration;
    }

    @Override
    public Flux<String> init() {
        return Flux.fromStream(hostsConfiguration.getInitHosts())
                .doOnNext(host -> webClientFacade.init(host+"/init"));
    }
}
