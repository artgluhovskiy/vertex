package org.art.vertex.obsidian.infrastructure.config;

import org.art.vertex.obsidian.domain.service.ObsidianLinkResolver;
import org.art.vertex.obsidian.domain.service.ObsidianNoteParser;
import org.art.vertex.obsidian.infrastructure.parser.DefaultObsidianLinkResolver;
import org.art.vertex.obsidian.infrastructure.parser.DefaultObsidianMetadataExtractor;
import org.art.vertex.obsidian.infrastructure.parser.DefaultObsidianNoteParser;
import org.art.vertex.obsidian.infrastructure.parser.ObsidianMetadataExtractor;
import org.art.vertex.obsidian.infrastructure.reader.DefaultObsidianFileReader;
import org.art.vertex.obsidian.infrastructure.reader.ObsidianFileReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yaml.snakeyaml.Yaml;

@Configuration(proxyBeanMethods = false)
public class ObsidianInfrastructureConfig {

    @Bean
    public ObsidianFileReader obsidianFileReader() {
        return new DefaultObsidianFileReader();
    }

    @Bean
    public Yaml yaml() {
        return new Yaml();
    }

    @Bean
    public ObsidianMetadataExtractor obsidianMetadataExtractor(Yaml yaml) {
        return new DefaultObsidianMetadataExtractor(yaml);
    }

    @Bean
    public ObsidianNoteParser obsidianNoteParser(ObsidianMetadataExtractor obsidianMetadataExtractor) {
        return new DefaultObsidianNoteParser(obsidianMetadataExtractor);
    }

    @Bean
    public ObsidianLinkResolver obsidianLinkResolver() {
        return new DefaultObsidianLinkResolver();
    }
}
