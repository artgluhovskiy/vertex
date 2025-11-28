package org.art.vertex.obsidian.application;

import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * Result of an Obsidian vault migration operation.
 * <p>
 * Contains statistics and errors from the migration process.
 */
@Value
@Builder
public class MigrationResult {

    int totalFiles;
    int notesCreated;
    int directoriesCreated;
    int linksCreated;
    int linksFailed;
    long durationMs;

    @Builder.Default
    List<MigrationError> errors = new ArrayList<>();

    /**
     * Individual migration error.
     */
    @Value
    @Builder
    public static class MigrationError {
        String file;
        String message;
    }
}
