package org.art.vertex.web.note.config;

import org.art.vertex.application.note.NoteApplicationService;
import org.art.vertex.web.note.NoteController;
import org.art.vertex.web.note.mapper.NoteCommandMapper;
import org.art.vertex.web.note.mapper.NoteDtoMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class NoteWebConfig {

    @Bean
    public NoteCommandMapper noteCommandMapper() {
        return new NoteCommandMapper();
    }

    @Bean
    public NoteDtoMapper noteDtoMapper() {
        return new NoteDtoMapper();
    }

    @Bean
    public NoteController noteController(
        NoteApplicationService noteApplicationService,
        NoteCommandMapper noteCommandMapper,
        NoteDtoMapper noteDtoMapper
    ) {
        return new NoteController(noteApplicationService, noteCommandMapper, noteDtoMapper);
    }
}
