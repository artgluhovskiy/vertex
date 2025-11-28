package org.art.vertex.obsidian.api.mapper;

import org.art.vertex.obsidian.api.dto.MigrationResultDto;
import org.art.vertex.obsidian.application.MigrationResult;

/**
 * Mapper for converting between migration domain models and DTOs.
 */
public class MigrationMapper {

    public static MigrationResultDto toDto(MigrationResult result) {
        return MigrationResultDto.builder()
            .totalFiles(result.getTotalFiles())
            .notesCreated(result.getNotesCreated())
            .directoriesCreated(result.getDirectoriesCreated())
            .linksCreated(result.getLinksCreated())
            .linksFailed(result.getLinksFailed())
            .durationMs(result.getDurationMs())
            .errors(result.getErrors().stream()
                .map(MigrationMapper::toErrorDto)
                .toList())
            .build();
    }

    private static MigrationResultDto.MigrationErrorDto toErrorDto(MigrationResult.MigrationError error) {
        return MigrationResultDto.MigrationErrorDto.builder()
            .file(error.getFile())
            .message(error.getMessage())
            .build();
    }
}
