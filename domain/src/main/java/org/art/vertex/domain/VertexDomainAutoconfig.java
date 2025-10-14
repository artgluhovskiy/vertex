package org.art.vertex.domain;

import org.art.vertex.domain.shared.uuid.TimeBasedUuidGenerator;
import org.art.vertex.domain.shared.uuid.UuidGenerator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class VertexDomainAutoconfig {

    @Bean
    public UuidGenerator uuidGenerator() {
        return new TimeBasedUuidGenerator();
    }
}
