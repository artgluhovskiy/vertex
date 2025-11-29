package org.art.vertex.obsidian.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.art.vertex.obsidian.api.dto.MigrationRequest;
import org.art.vertex.obsidian.api.dto.MigrationResultDto;
import org.art.vertex.obsidian.api.mapper.MigrationMapper;
import org.art.vertex.obsidian.application.MigrationResult;
import org.art.vertex.obsidian.application.ObsidianMigrationApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Paths;
import java.util.UUID;

/**
 * REST API controller for Obsidian vault migration.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/migration/obsidian")
@RequiredArgsConstructor
public class ObsidianMigrationController {

    private final ObsidianMigrationApplicationService migrationService;
    private final MigrationMapper mapper;

    /**
     * Migrate Obsidian vault to Synapse.
     *
     * @param userId  authenticated user ID
     * @param request migration request with vault path
     * @return migration result with statistics
     */
    @PostMapping
    public ResponseEntity<MigrationResultDto> migrateVault(
        @AuthenticationPrincipal UUID userId,
        @Valid @RequestBody MigrationRequest request
    ) {
        log.info("Migration request received. User id: {}, vault: {}", userId, request.getVaultPath());

        MigrationResult result = migrationService.migrateVault(userId, Paths.get(request.getVaultPath()));
        MigrationResultDto dto = mapper.toDto(result);

        log.info("Migration completed. User id: {}, notes created: {}", userId, result.getNotesCreated());

        return ResponseEntity.ok(dto);
    }
}
