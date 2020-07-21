package com.timmytime.predictorscraperreactive.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.timmytime.predictorscraperreactive.configuration.SportScraperConfiguration;
import com.timmytime.predictorscraperreactive.enumerator.ScraperTypeKeys;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class SportsScraperConfigurationFactory {

    private final Logger log = LoggerFactory.getLogger(SportsScraperConfigurationFactory.class);

    private final Map<ScraperTypeKeys, SportScraperConfiguration> sportScraperConfigurationMap = new HashMap<>();
    private final String configDir;

    @Autowired
    public SportsScraperConfigurationFactory(
            @Value("${config.dir}") String configDir) {

        this.configDir = configDir;
        load(ScraperTypeKeys.LINEUPS, "lineup-scraper-rules.yml");
        load(ScraperTypeKeys.MATCHES, "match-scraper-rules.yml");
        load(ScraperTypeKeys.PLAYER_STATS, "player-match-stats-rules.yml");
        load(ScraperTypeKeys.RESULTS, "results-scraper-rules.yml");

    }

    public SportScraperConfiguration getConfig(ScraperTypeKeys scraperTypeKeys) {
        return sportScraperConfigurationMap.get(scraperTypeKeys);
    }

    private void load(ScraperTypeKeys scraperTypeKeys, String fileName) {
        FileSystemResource fileSystemResource = new FileSystemResource(configDir + fileName);

        try {
            String yml = FileUtils.readFileToString(fileSystemResource.getFile());

            final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            SportScraperConfiguration configuration = mapper.readValue(yml, SportScraperConfiguration.class);


            sportScraperConfigurationMap.put(scraperTypeKeys, configuration);
        } catch (IOException e) {
            log.error("loading error: " + fileName, e);
        }
    }

}
