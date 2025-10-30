package org.art.vertex.infrastructure.note.config;

import org.art.vertex.domain.directory.DirectoryRepository;
import org.art.vertex.domain.note.NoteRepository;
import org.art.vertex.domain.user.UserRepository;
import org.art.vertex.infrastructure.note.DefaultNoteRepository;
import org.art.vertex.infrastructure.note.entity.NoteEntity;
import org.art.vertex.infrastructure.note.jpa.NoteJpaRepository;
import org.art.vertex.infrastructure.note.mapper.NoteEntityMapper;
import org.art.vertex.infrastructure.note.updater.NoteUpdater;
import org.art.vertex.infrastructure.tag.entity.TagEntity;
import org.art.vertex.infrastructure.tag.jpa.TagJpaRepository;
import org.art.vertex.infrastructure.tag.mapper.TagEntityMapper;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration(proxyBeanMethods = false)
@EnableJpaRepositories(basePackageClasses = {
    NoteJpaRepository.class,
    TagJpaRepository.class
})
@EntityScan(basePackageClasses = {
    NoteEntity.class,
    TagEntity.class
})
public class NoteInfrastructureConfig {

    @Bean
    public TagEntityMapper tagEntityMapper() {
        return new TagEntityMapper();
    }

    @Bean
    public NoteEntityMapper noteEntityMapper(
        DirectoryRepository directoryRepository,
        TagEntityMapper tagEntityMapper
    ) {
        return new NoteEntityMapper(directoryRepository, tagEntityMapper);
    }

    @Bean
    public NoteUpdater noteUpdater(TagEntityMapper tagEntityMapper) {
        return new NoteUpdater(tagEntityMapper);
    }

    @Bean
    public NoteRepository noteRepository(
        NoteJpaRepository noteJpaRepository,
        NoteEntityMapper noteEntityMapper,
        NoteUpdater noteUpdater
    ) {
        return new DefaultNoteRepository(noteJpaRepository, noteEntityMapper, noteUpdater);
    }
}
