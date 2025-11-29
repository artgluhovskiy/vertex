package org.art.vertex.infrastructure.note.link.config;

import org.art.vertex.domain.note.NoteLinkRepository;
import org.art.vertex.domain.note.NoteRepository;
import org.art.vertex.domain.user.UserRepository;
import org.art.vertex.infrastructure.note.link.DefaultNoteLinkRepository;
import org.art.vertex.infrastructure.note.link.entity.NoteLinkEntity;
import org.art.vertex.infrastructure.note.link.jpa.NoteLinkJpaRepository;
import org.art.vertex.infrastructure.note.link.mapper.NoteLinkEntityMapper;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration(proxyBeanMethods = false)
@EnableJpaRepositories(basePackageClasses = NoteLinkJpaRepository.class)
@EntityScan(basePackageClasses = NoteLinkEntity.class)
public class NoteLinkInfrastructureConfig {

    @Bean
    public NoteLinkEntityMapper noteLinkEntityMapper(
        NoteRepository noteRepository,
        UserRepository userRepository
    ) {
        return new NoteLinkEntityMapper(noteRepository, userRepository);
    }

    @Bean
    public NoteLinkRepository noteLinkRepository(
        NoteLinkJpaRepository noteLinkJpaRepository,
        NoteLinkEntityMapper noteLinkEntityMapper
    ) {
        return new DefaultNoteLinkRepository(noteLinkJpaRepository, noteLinkEntityMapper);
    }
}
