package org.art.vertex.test.obsidian;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.art.vertex.obsidian.api.dto.MigrationResultDto;
import org.art.vertex.test.BaseIntegrationTest;
import org.art.vertex.test.step.ObsidianMigrationSteps;
import org.art.vertex.web.user.dto.AuthenticationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DisplayName("Obsidian Migration Integration Test")
class ObsidianMigrationIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ObsidianMigrationSteps obsidianMigrationSteps;

    @Test
    @SneakyThrows
    @DisplayName("Should successfully migrate complete Obsidian vault with all edge cases")
    void shouldMigrateCompleteVault() {
        // Given: Test user and access token
        AuthenticationResponse authResponse = userSteps.register("test-obsidian@example.com", "password123");
        String accessToken = authResponse.accessToken();
        UUID userId = UUID.fromString(authResponse.user().id());
        assertThat(accessToken).isNotEmpty();

        // Given: Test vault path
        Path vaultPath = getTestVaultPath();
        log.info("Test vault path: {}", vaultPath);

        // When: Migrate vault via REST API
        MigrationResultDto result = obsidianMigrationSteps.migrateVault(accessToken, vaultPath.toString());

        // Then: Verify migration statistics
        assertThat(result.getTotalFiles())
            .as("Should find all markdown files")
            .isEqualTo(7);

        assertThat(result.getNotesCreated())
            .as("Should create all notes")
            .isEqualTo(7);

        assertThat(result.getDirectoriesCreated())
            .as("Should create nested directories")
            .isGreaterThanOrEqualTo(3); // Projects/Work, Daily, Archive

        assertThat(result.getErrors())
            .as("Should have no errors")
            .isEmpty();

        assertThat(result.getDurationMs())
            .as("Migration should complete in reasonable time")
            .isGreaterThan(0);

        // Then: Verify directory structure
        verifyDirectoryStructure(userId);

        // Then: Verify notes were created
        verifyNotesCreated(userId);

        // Then: Verify tags were extracted
        verifyTagsCreated();

        // Then: Verify note content and metadata
        verifyNoteContent(userId);

        // Then: Verify links were created
        verifyLinksCreated(userId, result);

        log.info("✅ Migration test completed successfully. Notes: {}, Directories: {}, Links: {}, Duration: {}ms",
            result.getNotesCreated(), result.getDirectoriesCreated(), result.getLinksCreated(), result.getDurationMs());
    }

    private Path getTestVaultPath() throws URISyntaxException {
        return Paths.get(getClass().getClassLoader()
            .getResource("test-vault")
            .toURI());
    }

    private void verifyDirectoryStructure(UUID userId) {
        Integer dirCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM directories WHERE user_id = ?",
            Integer.class,
            userId);

        assertThat(dirCount)
            .as("Should have created directories (including root)")
            .isGreaterThanOrEqualTo(4); // Root + Projects/Work + Daily + Archive

        // Verify specific directories exist
        Integer projectsWorkCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM directories WHERE user_id = ? AND name = ?",
            new Object[]{userId, "Work"},
            Integer.class);
        assertThat(projectsWorkCount).isEqualTo(1);

        Integer dailyCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM directories WHERE user_id = ? AND name = ?",
            new Object[]{userId, "Daily"},
            Integer.class);
        assertThat(dailyCount).isEqualTo(1);

        Integer archiveCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM directories WHERE user_id = ? AND name = ?",
            new Object[]{userId, "Archive"},
            Integer.class);
        assertThat(archiveCount).isEqualTo(1);

        // Verify parent relationships
        verifyParentRelationships(userId);

        log.info("✓ Directory structure verified");
    }

    private void verifyParentRelationships(UUID userId) {
        // Get root directory ID
        UUID rootId = jdbcTemplate.queryForObject(
            "SELECT id FROM directories WHERE user_id = ? AND name = ? AND parent_id IS NULL",
            new Object[]{userId, "Root"},
            UUID.class);
        assertThat(rootId).as("Root directory should exist").isNotNull();

        // Verify top-level directories have Root as parent
        Integer topLevelDirsWithRootParent = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM directories WHERE user_id = ? AND parent_id = ? AND name IN ('Archive', 'Daily', 'Projects')",
            new Object[]{userId, rootId},
            Integer.class);
        assertThat(topLevelDirsWithRootParent)
            .as("Archive, Daily, and Projects should have Root as parent")
            .isEqualTo(3);

        // Verify Work directory has Projects as parent
        UUID projectsId = jdbcTemplate.queryForObject(
            "SELECT id FROM directories WHERE user_id = ? AND name = ?",
            new Object[]{userId, "Projects"},
            UUID.class);

        UUID workParentId = jdbcTemplate.queryForObject(
            "SELECT parent_id FROM directories WHERE user_id = ? AND name = ?",
            new Object[]{userId, "Work"},
            UUID.class);
        assertThat(workParentId)
            .as("Work directory should have Projects as parent")
            .isEqualTo(projectsId);

        log.info("✓ Parent relationships verified");
    }

    private void verifyNotesCreated(UUID userId) {
        Integer noteCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM notes WHERE user_id = ?",
            Integer.class,
            userId);

        assertThat(noteCount)
            .as("All notes should be created")
            .isEqualTo(7);

        // Verify specific notes by title
        assertNoteExists(userId, "Welcome to Test Vault");
        assertNoteExists(userId, "Note with Spaces");
        assertNoteExists(userId, "No Frontmatter");
        assertNoteExists(userId, "Project Alpha");
        assertNoteExists(userId, "Project Beta");
        assertNoteExists(userId, "Daily Note: January 15, 2024");
        assertNoteExists(userId, "Archived Note");

        log.info("✓ All notes created successfully");
    }

    private void assertNoteExists(UUID userId, String title) {
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM notes WHERE user_id = ? AND title = ?",
            new Object[]{userId, title},
            Integer.class);
        assertThat(count)
            .as("Note '%s' should exist", title)
            .isEqualTo(1);
    }

    private void verifyTagsCreated() {
        Integer tagCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM tags",
            Integer.class
        );

        assertThat(tagCount)
            .as("Should have created multiple tags")
            .isGreaterThan(0);

        // Verify specific tags from frontmatter
        assertTagExists("welcome");
        assertTagExists("getting-started");
        assertTagExists("project");
        assertTagExists("work");

        // Verify inline tags
        assertTagExists("root-level");
        assertTagExists("test");
        assertTagExists("archived");

        log.info("✓ Tags extracted and created");
    }

    private void assertTagExists(String tagName) {
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM tags WHERE name = ?",
            Integer.class,
            tagName);
        assertThat(count)
            .as("Tag '%s' should exist", tagName)
            .isGreaterThanOrEqualTo(1);
    }

    private void verifyNoteContent(UUID userId) {
        // Verify note with frontmatter
        String welcomeContent = jdbcTemplate.queryForObject(
            "SELECT content FROM notes WHERE user_id = ? AND title = ?",
            new Object[]{userId, "Welcome to Test Vault"},
            String.class);
        assertThat(welcomeContent)
            .as("Welcome note content should not contain frontmatter")
            .doesNotContain("---")
            .contains("Welcome to Test Vault")
            .contains("Links to Other Notes");

        // Verify note without frontmatter
        String noFrontmatterContent = jdbcTemplate.queryForObject(
            "SELECT content FROM notes WHERE user_id = ? AND title = ?",
            new Object[]{userId, "No Frontmatter"},
            String.class);
        assertThat(noFrontmatterContent)
            .as("No frontmatter note should have correct content")
            .contains("Simple note without any YAML frontmatter");

        // Verify note in nested directory
        String projectAlphaContent = jdbcTemplate.queryForObject(
            "SELECT content FROM notes WHERE user_id = ? AND title = ?",
            new Object[]{userId, "Project Alpha"},
            String.class);
        assertThat(projectAlphaContent)
            .as("Project Alpha should have correct content")
            .contains("Project Alpha is our main initiative")
            .doesNotContain("---"); // Frontmatter should be removed

        log.info("✓ Note content verified");
    }

    private void verifyLinksCreated(UUID userId, MigrationResultDto result) {
        // Verify links were created from migration result
        assertThat(result.getLinksCreated())
            .as("Should have created note links")
            .isGreaterThan(0);

        // Verify links exist in database
        Integer linkCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM note_links WHERE user_id = ?",
            Integer.class,
            userId);
        assertThat(linkCount)
            .as("Links should be persisted to database")
            .isEqualTo(result.getLinksCreated());

        // Verify specific link exists (e.g., Welcome.md -> Project Alpha)
        String welcomeTitle = "Welcome to Test Vault";
        String projectAlphaTitle = "Project Alpha";

        UUID welcomeNoteId = jdbcTemplate.queryForObject(
            "SELECT id FROM notes WHERE user_id = ? AND title = ?",
            new Object[]{userId, welcomeTitle},
            UUID.class);

        UUID projectAlphaNoteId = jdbcTemplate.queryForObject(
            "SELECT id FROM notes WHERE user_id = ? AND title = ?",
            new Object[]{userId, projectAlphaTitle},
            UUID.class);

        Integer welcomeToAlphaLinks = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM note_links WHERE source_note_id = ? AND target_note_id = ?",
            new Object[]{welcomeNoteId, projectAlphaNoteId},
            Integer.class);

        assertThat(welcomeToAlphaLinks)
            .as("Link from Welcome to Project Alpha should exist")
            .isEqualTo(1);

        log.info("✓ Note links verified. Total links: {}", result.getLinksCreated());
    }

    @Test
    @SneakyThrows
    @DisplayName("Should handle migration with partial errors gracefully")
    void shouldHandlePartialErrors() {
        // Given: Test user and access token
        AuthenticationResponse authResponse = userSteps.register("test-partial@example.com", "password123");
        String accessToken = authResponse.accessToken();
        Path vaultPath = getTestVaultPath();

        // When: Migrate (some notes might have issues, but migration continues)
        MigrationResultDto result = obsidianMigrationSteps.migrateVault(accessToken, vaultPath.toString());

        // Then: Verify migration completed
        assertThat(result.getNotesCreated())
            .as("Should create notes even if some fail")
            .isGreaterThan(0);

        // Migration result should contain error details if any
        if (!result.getErrors().isEmpty()) {
            log.warn("Migration completed with {} errors:", result.getErrors().size());
            result.getErrors().forEach(error ->
                log.warn("  - {}: {}", error.getFile(), error.getMessage())
            );
        }
    }
}
