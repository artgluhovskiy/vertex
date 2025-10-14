package org.art.vertex.infrastructure.directory.config;

import org.art.vertex.domain.directory.DirectoryRepository;
import org.art.vertex.infrastructure.directory.DefaultDirectoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class DirectoryInfrastructureConfig {

    @Bean
    public DirectoryRepository directoryRepository() {
        return new DefaultDirectoryRepository();
    }
}
