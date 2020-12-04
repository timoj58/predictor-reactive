package com.timmytime.predictoreventdatareactive.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.timmytime.predictoreventdatareactive.configuration.SpecialCase;
import com.timmytime.predictoreventdatareactive.configuration.SpecialCasesConfiguration;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class SpecialCasesFactory {

    private final Logger log = LoggerFactory.getLogger(SpecialCasesFactory.class);
    private final String configDir;
    private final SpecialCasesConfiguration specialCasesConfiguration;

    @Autowired
    public SpecialCasesFactory(
            @Value("${config.dir}") String configDir
    ) {
        this.configDir = configDir;
        this.specialCasesConfiguration = load("specialcases.yml");
    }

    private SpecialCasesConfiguration load(String fileName) {
        FileSystemResource fileSystemResource = new FileSystemResource(configDir + fileName);

        try {
            String yml = FileUtils.readFileToString(fileSystemResource.getFile());

            final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            return mapper.readValue(yml, SpecialCasesConfiguration.class);


        } catch (IOException e) {
            log.error("loading error: " + fileName, e);
        }
        return null;
    }

    public SpecialCasesConfiguration getSpecialCasesConfiguration() {
        return specialCasesConfiguration;
    }

    public Optional<SpecialCase> getSpecialCase(String alias) {
        return specialCasesConfiguration.getSpecialCases().stream().filter(f -> f.getAlias().equals(alias)).findFirst();
    }


}
