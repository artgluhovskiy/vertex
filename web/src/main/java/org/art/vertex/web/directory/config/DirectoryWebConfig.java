package org.art.vertex.web.directory.config;

import org.art.vertex.application.directory.DirectoryApplicationService;
import org.art.vertex.web.directory.DirectoryController;
import org.art.vertex.web.directory.mapper.DirectoryCommandMapper;
import org.art.vertex.web.directory.mapper.DirectoryDtoMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class DirectoryWebConfig {

    @Bean
    public DirectoryCommandMapper directoryCommandMapper() {
        return new DirectoryCommandMapper();
    }

    @Bean
    public DirectoryDtoMapper directoryDtoMapper() {
        return new DirectoryDtoMapper();
    }

    @Bean
    public DirectoryController directoryController(
        DirectoryApplicationService directoryApplicationService,
        DirectoryCommandMapper directoryCommandMapper,
        DirectoryDtoMapper directoryDtoMapper
    ) {
        return new DirectoryController(directoryApplicationService, directoryCommandMapper, directoryDtoMapper);
    }
}
