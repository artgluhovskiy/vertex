package org.art.vertex.application.note.config;

import org.art.vertex.application.note.DefaultNoteApplicationService;
import org.art.vertex.application.note.NoteApplicationService;
import org.art.vertex.application.tag.TagApplicationService;
import org.art.vertex.domain.directory.DirectoryRepository;
import org.art.vertex.domain.note.NoteRepository;
import org.art.vertex.domain.shared.time.Clock;
import org.art.vertex.domain.shared.uuid.UuidGenerator;
import org.art.vertex.domain.user.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class NoteApplicationConfig {

    @Bean
    public NoteApplicationService noteApplicationService(
        UserRepository userRepository,
        NoteRepository noteRepository,
        DirectoryRepository directoryRepository,
        TagApplicationService tagApplicationService,
        UuidGenerator uuidGenerator,
        Clock clock
    ) {
        return new DefaultNoteApplicationService(
            userRepository,
            noteRepository,
            directoryRepository,
            tagApplicationService,
            uuidGenerator,
            clock
        );
    }
}
