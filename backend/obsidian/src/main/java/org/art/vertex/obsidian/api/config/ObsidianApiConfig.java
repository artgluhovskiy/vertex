package org.art.vertex.obsidian.api.config;

import org.art.vertex.obsidian.api.controller.ObsidianMigrationController;
import org.art.vertex.obsidian.api.mapper.MigrationMapper;
import org.art.vertex.obsidian.application.ObsidianMigrationApplicationService;
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
    public ObsidianMigrationController obsidianMigrationController(
        ObsidianMigrationApplicationService migrationService,
        MigrationMapper mapper
    ) {
        return new ObsidianMigrationController(migrationService, mapper);
    }
}
