package org.art.vertex.obsidian.api.config;

import org.art.vertex.obsidian.api.controller.ObsidianMigrationController;
import org.art.vertex.obsidian.application.ObsidianMigrationApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * API configuration for Obsidian migration REST controllers.
 */
@Configuration(proxyBeanMethods = false)
public class ObsidianApiConfig {

    @Bean
    public ObsidianMigrationController obsidianMigrationController(
        ObsidianMigrationApplicationService migrationService
    ) {
        return new ObsidianMigrationController(migrationService);
    }
}
