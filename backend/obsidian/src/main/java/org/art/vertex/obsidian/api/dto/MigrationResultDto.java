package org.art.vertex.obsidian.api.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * Response containing migration result statistics.
 */
@Value
@Builder
public class MigrationResultDto {

    int totalFiles;
    int notesCreated;
    int directoriesCreated;
    int linksCreated;
    int linksFailed;
    long durationMs;
    List<MigrationErrorDto> errors;

    /**
     * Individual migration error.
     */
    @Value
    @Builder
    public static class MigrationErrorDto {
        String file;
        String message;
    }
}
