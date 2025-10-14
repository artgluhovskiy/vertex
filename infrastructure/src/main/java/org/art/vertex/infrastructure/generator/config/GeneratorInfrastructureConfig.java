package org.art.vertex.infrastructure.generator.config;

import org.art.vertex.domain.shared.generator.UuidGenerator;
import org.art.vertex.infrastructure.generator.TimeBasedUuidGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeneratorInfrastructureConfig {

    @Bean
    public UuidGenerator uuidGenerator() {
        return new TimeBasedUuidGenerator();
    }
}
