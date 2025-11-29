package org.art.vertex.obsidian.application.config;

import org.art.vertex.application.directory.DirectoryApplicationService;
import org.art.vertex.application.note.NoteApplicationService;
import org.art.vertex.application.tag.TagApplicationService;
import org.art.vertex.obsidian.application.DefaultObsidianNoteParser;
import org.art.vertex.obsidian.application.ObsidianMigrationApplicationService;
import org.art.vertex.obsidian.application.parser.DefaultObsidianLinkResolver;
import org.art.vertex.obsidian.application.parser.DefaultObsidianMetadataExtractor;
import org.art.vertex.obsidian.application.parser.ObsidianMetadataExtractor;
import org.art.vertex.obsidian.domain.service.ObsidianFileReader;
import org.art.vertex.obsidian.domain.service.ObsidianLinkResolver;
import org.art.vertex.obsidian.domain.service.ObsidianNoteParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yaml.snakeyaml.Yaml;

@Configuration(proxyBeanMethods = false)
public class ObsidianApplicationConfig {

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

    @Bean
    public ObsidianMigrationApplicationService obsidianMigrationApplicationService(
        ObsidianFileReader fileReader,
        ObsidianNoteParser noteParser,
        ObsidianLinkResolver linkResolver,
        NoteApplicationService noteService,
        DirectoryApplicationService directoryService,
        TagApplicationService tagService
    ) {
        return new ObsidianMigrationApplicationService(
            fileReader,
            noteParser,
            linkResolver,
            noteService,
            directoryService,
            tagService
        );
    }
}
