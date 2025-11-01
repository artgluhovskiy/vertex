package org.art.vertex.application.directory.config;

import org.art.vertex.application.directory.DirectoryApplicationService;
import org.art.vertex.domain.directory.DirectoryRepository;
import org.art.vertex.domain.shared.time.Clock;
import org.art.vertex.domain.shared.uuid.UuidGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class DirectoryApplicationConfig {

    @Bean
    public DirectoryApplicationService directoryApplicationService(
        DirectoryRepository directoryRepository,
        UuidGenerator uuidGenerator,
        Clock clock
    ) {
        return new DirectoryApplicationService(
            directoryRepository,
            uuidGenerator,
            clock
        );
    }
}
