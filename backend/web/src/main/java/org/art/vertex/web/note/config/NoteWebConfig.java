package org.art.vertex.web.note.config;

import org.art.vertex.application.note.NoteApplicationService;
import org.art.vertex.application.note.search.NoteSearchApplicationService;
import org.art.vertex.web.note.NoteController;
import org.art.vertex.web.note.mapper.NoteCommandMapper;
import org.art.vertex.web.note.mapper.NoteDtoMapper;
import org.art.vertex.web.note.search.NoteSearchController;
import org.art.vertex.web.note.search.mapper.SearchCommandMapper;
import org.art.vertex.web.note.search.mapper.SearchDtoMapper;
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

    @Bean
    public SearchCommandMapper searchCommandMapper() {
        return new SearchCommandMapper();
    }

    @Bean
    public SearchDtoMapper searchDtoMapper(NoteDtoMapper noteDtoMapper) {
        return new SearchDtoMapper(noteDtoMapper);
    }

    @Bean
    public NoteSearchController searchController(
        NoteSearchApplicationService noteSearchApplicationService,
        SearchCommandMapper searchCommandMapper,
        SearchDtoMapper searchDtoMapper
    ) {
        return new NoteSearchController(noteSearchApplicationService, searchCommandMapper, searchDtoMapper);
    }
}
