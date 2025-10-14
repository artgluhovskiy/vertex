package org.art.vertex.application.note.config;

import org.art.vertex.application.note.DefaultNoteApplicationService;
import org.art.vertex.application.note.NoteApplicationService;
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
        NoteRepository noteRepository,
        UserRepository userRepository,
        UuidGenerator uuidGenerator,
        Clock clock
    ) {
        return new DefaultNoteApplicationService(noteRepository, userRepository, uuidGenerator, clock);
    }
}
