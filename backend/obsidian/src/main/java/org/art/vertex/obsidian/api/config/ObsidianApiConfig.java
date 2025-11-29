package org.art.vertex.obsidian.api.config;

import org.art.vertex.domain.shared.time.Clock;
import org.art.vertex.obsidian.api.exception.ObsidianApiExceptionHandler;
import org.art.vertex.obsidian.api.mapper.MigrationMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * API configuration for Obsidian migration REST controllers.
 */
@Configuration(proxyBeanMethods = false)
public class ObsidianApiConfig {

    @Bean
    public MigrationMapper migrationMapper() {
        return new MigrationMapper();
    }

    @Bean
    public ObsidianApiExceptionHandler obsidianApiExceptionHandler(Clock clock) {
        return new ObsidianApiExceptionHandler(clock);
    }
}
