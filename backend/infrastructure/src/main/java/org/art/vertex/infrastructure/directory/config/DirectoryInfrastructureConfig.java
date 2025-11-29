package org.art.vertex.infrastructure.directory.config;

import org.art.vertex.domain.directory.DirectoryRepository;
import org.art.vertex.infrastructure.directory.DefaultDirectoryRepository;
import org.art.vertex.infrastructure.directory.entity.DirectoryEntity;
import org.art.vertex.infrastructure.directory.jpa.DirectoryJpaRepository;
import org.art.vertex.infrastructure.directory.mapper.DirectoryEntityMapper;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration(proxyBeanMethods = false)
@EnableJpaRepositories(basePackageClasses = DirectoryJpaRepository.class)
@EntityScan(basePackageClasses = DirectoryEntity.class)
public class DirectoryInfrastructureConfig {

    @Bean
    public DirectoryEntityMapper directoryEntityMapper(DirectoryJpaRepository directoryJpaRepository) {
        return new DirectoryEntityMapper(directoryJpaRepository);
    }

    @Bean
    public DirectoryRepository directoryRepository(
        DirectoryJpaRepository directoryJpaRepository,
        DirectoryEntityMapper directoryEntityMapper
    ) {
        return new DefaultDirectoryRepository(directoryJpaRepository, directoryEntityMapper);
    }
}

