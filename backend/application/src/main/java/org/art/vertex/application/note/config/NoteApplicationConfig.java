package org.art.vertex.application.note.config;

import org.art.vertex.application.directory.DirectoryApplicationService;
import org.art.vertex.application.note.NoteApplicationService;
import org.art.vertex.application.tag.TagApplicationService;
import org.art.vertex.application.user.UserApplicationService;
import org.art.vertex.domain.note.NoteRepository;
import org.art.vertex.domain.shared.time.Clock;
import org.art.vertex.domain.shared.uuid.UuidGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class NoteApplicationConfig {

    @Bean
    public NoteApplicationService noteApplicationService(
        UserApplicationService userApplicationService,
        NoteRepository noteRepository,
        TagApplicationService tagApplicationService,
        DirectoryApplicationService directoryApplicationService,
        UuidGenerator uuidGenerator,
        Clock clock
    ) {
        return new NoteApplicationService(
            userApplicationService,
            directoryApplicationService,
            tagApplicationService,
            noteRepository,
            uuidGenerator,
            clock
        );
    }
}
