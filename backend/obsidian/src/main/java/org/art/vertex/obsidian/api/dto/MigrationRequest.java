package org.art.vertex.obsidian.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

/**
 * Request to migrate an Obsidian vault.
 */
@Value
@Builder
public class MigrationRequest {

    /**
     * Absolute path to Obsidian vault root directory.
     */
    @NotBlank(message = "Vault path is required")
    String vaultPath;
}
