package com.timmytime.predictoreventscraperreactive.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.timmytime.predictoreventscraperreactive.configuration.BookmakerScraperConfiguration;
import com.timmytime.predictoreventscraperreactive.enumerator.ScraperTypeKeys;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class BookmakerScraperConfigurationFactory {


    private final Map<ScraperTypeKeys, BookmakerScraperConfiguration> bookmakerScraperConfigurationHashMap = new HashMap<>();
    private final String configDir;


    @Autowired
    public BookmakerScraperConfigurationFactory(
            @Value("${config.dir}") String configDir
    ) {
        this.configDir = configDir;
        load(ScraperTypeKeys.PADDYPOWER_ODDS, "paddypower-odds.yml");
        load(ScraperTypeKeys.BETWAY_ODDS, "betway-odds.yml");
    }

    public BookmakerScraperConfiguration getConfig(ScraperTypeKeys scraperTypeKeys) {
        return bookmakerScraperConfigurationHashMap.get(scraperTypeKeys);
    }

    private void load(ScraperTypeKeys scraperTypeKeys, String fileName) {
        FileSystemResource fileSystemResource = new FileSystemResource(configDir + fileName);

        try {
            String yml = FileUtils.readFileToString(fileSystemResource.getFile());

            final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            BookmakerScraperConfiguration configuration = mapper.readValue(yml, BookmakerScraperConfiguration.class);


            bookmakerScraperConfigurationHashMap.put(scraperTypeKeys, configuration);
        } catch (IOException e) {
            log.error("loading error: " + fileName, e);
        }
    }

}
