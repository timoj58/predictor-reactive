package com.timmytime.predictormessagereactive.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Stream;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix="clients")
public class HostsConfiguration {
    private String dataScraper;
    private String eventsScraper;
    private String data;
    private String dataEvent;
    private String teams;
    private String players;
    private String teamEvents;
    private String playerEvents;
    private String client;

    public Stream<String> getInitHosts(){
        return Stream.of(dataScraper, data, dataEvent, teams, players, playerEvents);
    }
}
