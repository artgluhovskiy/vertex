package org.art.vertex.obsidian.application.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for Obsidian vault migration.
 */
@ConfigurationProperties("vertex.obsidian")
@Data
@Validated
public class ObsidianProperties {

    /**
     * Maximum vault size in MB to prevent memory issues.
     * Default: 1024 MB (1 GB)
     */
    @Min(value = 1, message = "Max vault size must be at least 1MB")
    @Max(value = 10240, message = "Max vault size cannot exceed 10GB")
    private int maxVaultSizeMb = 1024;

    /**
     * Maximum number of notes to process in single migration.
     * Default: 10000 notes
     */
    @Min(value = 1, message = "Max notes must be at least 1")
    @Max(value = 100000, message = "Max notes cannot exceed 100,000")
    private int maxNotes = 10000;

    /**
     * Whether to skip invalid markdown files during migration.
     * Default: true
     */
    private boolean skipInvalidFiles = true;

    /**
     * Migration timeout in seconds.
     * Default: 300 seconds (5 minutes)
     */
    @Min(value = 10, message = "Timeout must be at least 10 seconds")
    @Max(value = 3600, message = "Timeout cannot exceed 1 hour")
    private int migrationTimeoutSeconds = 300;

    /**
     * Markdown file extension.
     * Default: .md
     */
    @NotBlank(message = "Markdown extension cannot be blank")
    private String markdownExtension = ".md";

    /**
     * Batch size for note creation.
     * Default: 100 notes per batch
     */
    @Min(value = 10, message = "Batch size must be at least 10")
    @Max(value = 1000, message = "Batch size cannot exceed 1000")
    private int noteBatchSize = 100;
}
