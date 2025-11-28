package org.art.vertex.obsidian.application.config;

import org.art.vertex.application.directory.DirectoryApplicationService;
import org.art.vertex.application.note.NoteApplicationService;
import org.art.vertex.application.tag.TagApplicationService;
import org.art.vertex.obsidian.application.ObsidianMigrationApplicationService;
import org.art.vertex.obsidian.domain.service.ObsidianLinkResolver;
import org.art.vertex.obsidian.domain.service.ObsidianNoteParser;
import org.art.vertex.obsidian.infrastructure.reader.ObsidianFileReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Application configuration for Obsidian migration services.
 */
@Configuration(proxyBeanMethods = false)
public class ObsidianApplicationConfig {

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
