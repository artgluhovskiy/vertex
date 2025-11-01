package org.art.vertex.domain;

import org.art.vertex.domain.shared.time.Clock;
import org.art.vertex.domain.shared.uuid.TimeBasedUuidGenerator;
import org.art.vertex.domain.shared.uuid.UuidGenerator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class VertexDomainAutoconfig {

    @Bean
    public Clock clock() {
        return new Clock(java.time.Clock.systemUTC());
    }

    @Bean
    public UuidGenerator uuidGenerator() {
        return new TimeBasedUuidGenerator();
    }
}
