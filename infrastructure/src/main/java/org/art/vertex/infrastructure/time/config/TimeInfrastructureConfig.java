package org.art.vertex.infrastructure.time.config;

import org.art.vertex.domain.shared.time.Clock;
import org.art.vertex.infrastructure.time.SystemClock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class TimeInfrastructureConfig {

    @Bean
    public Clock clock() {
        return new SystemClock();
    }
}
