package org.art.vertex.application.note.config;

import org.art.vertex.application.directory.DirectoryApplicationService;
import org.art.vertex.application.note.NoteApplicationService;
import org.art.vertex.application.note.link.NoteLinkApplicationService;
import org.art.vertex.application.note.search.NoteSearchApplicationService;
import org.art.vertex.application.note.search.NoteIndexingApplicationService;
import org.art.vertex.application.tag.TagApplicationService;
import org.art.vertex.application.user.UserApplicationService;
import org.art.vertex.domain.note.NoteLinkRepository;
import org.art.vertex.domain.note.NoteRepository;
import org.art.vertex.domain.note.search.VectorSearchService;
import org.art.vertex.domain.shared.time.Clock;
import org.art.vertex.domain.shared.uuid.UuidGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration(proxyBeanMethods = false)
@EnableAsync
public class NoteApplicationConfig {

    @Bean
    public NoteIndexingApplicationService noteIndexingService(
        VectorSearchService vectorSearchService
    ) {
        return new NoteIndexingApplicationService(vectorSearchService);
    }

    @Bean
    public NoteApplicationService noteApplicationService(
        UserApplicationService userApplicationService,
        NoteRepository noteRepository,
        TagApplicationService tagApplicationService,
        DirectoryApplicationService directoryApplicationService,
        NoteIndexingApplicationService indexingService,
        UuidGenerator uuidGenerator,
        Clock clock,
        @Value("${search.indexing.async:true}") boolean asyncIndexing
    ) {
        return new NoteApplicationService(
            userApplicationService,
            directoryApplicationService,
            tagApplicationService,
            noteRepository,
            indexingService,
            uuidGenerator,
            clock,
            asyncIndexing
        );
    }

    @Bean
    public NoteSearchApplicationService searchApplicationService(
        VectorSearchService vectorSearchService
    ) {
        return new NoteSearchApplicationService(vectorSearchService);
    }

    @Bean
    public NoteLinkApplicationService noteLinkApplicationService(
        NoteLinkRepository noteLinkRepository,
        NoteApplicationService noteApplicationService,
        UserApplicationService userApplicationService,
        UuidGenerator uuidGenerator,
        Clock clock
    ) {
        return new NoteLinkApplicationService(
            noteLinkRepository,
            noteApplicationService,
            userApplicationService,
            uuidGenerator,
            clock
        );
    }
}
