package com.timmytime.predictordatareactive.configuration;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "data.missing")
public class DataConfig {
    private List<CountryConfig> countries;
}
