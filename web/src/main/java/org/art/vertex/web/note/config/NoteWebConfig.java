package org.art.vertex.web.note.config;

import org.art.vertex.application.note.NoteApplicationService;
import org.art.vertex.web.note.NoteController;
import org.art.vertex.web.note.mapper.NoteDtoMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class NoteWebConfig {

    @Bean
    public NoteDtoMapper noteDtoMapper() {
        return new NoteDtoMapper();
    }

    @Bean
    public NoteController noteController(
        NoteApplicationService noteApplicationService,
        NoteDtoMapper noteDtoMapper
    ) {
        return new NoteController(noteApplicationService, noteDtoMapper);
    }
}
