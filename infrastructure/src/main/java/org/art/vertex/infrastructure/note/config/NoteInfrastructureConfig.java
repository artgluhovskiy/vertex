package org.art.vertex.infrastructure.note.config;

import org.art.vertex.domain.note.NoteRepository;
import org.art.vertex.domain.user.UserRepository;
import org.art.vertex.infrastructure.note.DefaultNoteRepository;
import org.art.vertex.infrastructure.note.entity.NoteEntity;
import org.art.vertex.infrastructure.note.jpa.NoteJpaRepository;
import org.art.vertex.infrastructure.note.mapper.NoteEntityMapper;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration(proxyBeanMethods = false)
@EnableJpaRepositories(basePackageClasses = {
    NoteJpaRepository.class
})
@EntityScan(basePackageClasses = {
    NoteEntity.class
})
public class NoteInfrastructureConfig {

    @Bean
    public NoteEntityMapper noteEntityMapper(UserRepository userRepository) {
        return new NoteEntityMapper(userRepository);
    }

    @Bean
    public NoteRepository noteRepository(NoteJpaRepository noteJpaRepository, NoteEntityMapper noteEntityMapper) {
        return new DefaultNoteRepository(noteJpaRepository, noteEntityMapper);
    }
}
