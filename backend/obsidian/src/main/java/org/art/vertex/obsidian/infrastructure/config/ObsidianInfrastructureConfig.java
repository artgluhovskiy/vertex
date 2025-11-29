package org.art.vertex.obsidian.infrastructure.config;

import org.art.vertex.obsidian.domain.service.ObsidianFileReader;
import org.art.vertex.obsidian.infrastructure.reader.DefaultObsidianFileReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class ObsidianInfrastructureConfig {

    @Bean
    public ObsidianFileReader obsidianFileReader() {
        return new DefaultObsidianFileReader();
    }
}
