package org.art.vertex.obsidian.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.art.vertex.application.directory.DirectoryApplicationService;
import org.art.vertex.application.directory.command.CreateDirectoryCommand;
import org.art.vertex.application.note.NoteApplicationService;
import org.art.vertex.application.note.command.CreateNoteCommand;
import org.art.vertex.application.note.command.CreateNotesCommand;
import org.art.vertex.application.note.link.NoteLinkApplicationService;
import org.art.vertex.application.note.link.command.CreateNoteLinkCommand;
import org.art.vertex.application.tag.TagApplicationService;
import org.art.vertex.domain.directory.model.Directory;
import org.art.vertex.domain.note.model.Note;
import org.art.vertex.obsidian.domain.model.ObsidianNote;
import org.art.vertex.obsidian.domain.model.WikilinkReference;
import org.art.vertex.obsidian.domain.service.ObsidianLinkResolver;
import org.art.vertex.obsidian.domain.service.ObsidianNoteParser;
import org.art.vertex.obsidian.domain.service.ObsidianFileReader;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Application service for orchestrating Obsidian vault migration.
 * <p>
 * Migration process:
 * <ol>
 *   <li>Phase 1: Discovery - Find all markdown files</li>
 *   <li>Phase 2: Directory Structure - Create directory hierarchy</li>
 *   <li>Phase 3: Parse Notes - Parse all markdown files</li>
 *   <li>Phase 4: Create Notes - Import notes into Synapse</li>
 *   <li>Phase 5: Link Resolution - Create note links</li>
 * </ol>
 */
@Slf4j
@RequiredArgsConstructor
public class ObsidianMigrationApplicationService {

    private final ObsidianFileReader fileReader;
    private final ObsidianNoteParser noteParser;
    private final ObsidianLinkResolver linkResolver;
    private final NoteApplicationService noteService;
    private final DirectoryApplicationService directoryService;
    private final TagApplicationService tagService;
    private final NoteLinkApplicationService noteLinkService;

    /**
     * Migrate Obsidian vault to Synapse.
     *
     * @param userId    user ID to import notes for
     * @param vaultPath path to Obsidian vault root directory
     * @return migration result with statistics
     */
    @Transactional
    public MigrationResult migrateVault(UUID userId, Path vaultPath) {
        log.info("Starting Obsidian vault migration. User id: {}, vault: {}", userId, vaultPath);

        long startTime = System.currentTimeMillis();
        MigrationResultBuilder resultBuilder = new MigrationResultBuilder();

        try {
            List<Path> markdownFiles = discoverMarkdownFiles(vaultPath, resultBuilder);
            Map<String, Directory> directoryMap = createDirectoryStructure(userId, markdownFiles, vaultPath, resultBuilder);
            Map<Path, ObsidianNote> parsedNotes = parseNotes(markdownFiles, vaultPath, resultBuilder);
            Map<Path, Note> createdNotes = createNotes(userId, parsedNotes, directoryMap, resultBuilder);
            Map<UUID, List<UUID>> resolvedLinks = resolveLinks(parsedNotes, createdNotes, resultBuilder);
            createNoteLinks(userId, resolvedLinks, resultBuilder);

            long duration = System.currentTimeMillis() - startTime;

            log.info("Migration completed. Notes: {}, directories: {}, links: {}, duration: {}ms",
                resultBuilder.notesCreated, resultBuilder.directoriesCreated,
                resultBuilder.linksCreated, duration);

            return resultBuilder.build(duration);

        } catch (Exception e) {
            log.error("Migration failed. User id: {}, vault: {}", userId, vaultPath, e);
            resultBuilder.addError("Migration", "Failed: " + e.getMessage());
            long duration = System.currentTimeMillis() - startTime;
            return resultBuilder.build(duration);
        }
    }

    private List<Path> discoverMarkdownFiles(Path vaultPath, MigrationResultBuilder resultBuilder) {
        log.info("Phase 1: Discovering markdown files...");
        List<Path> markdownFiles = fileReader.discoverMarkdownFiles(vaultPath);
        resultBuilder.totalFiles = markdownFiles.size();
        log.info("Found {} markdown files", markdownFiles.size());
        return markdownFiles;
    }

    private Map<String, Directory> createDirectoryStructure(
        UUID userId,
        List<Path> markdownFiles,
        Path vaultPath,
        MigrationResultBuilder resultBuilder
    ) {
        log.info("Phase 2: Creating directory structure...");

        Set<String> directoryPaths = extractDirectoryPaths(markdownFiles, vaultPath);
        log.debug("Extracted directory paths: {}", directoryPaths);

        Map<String, Directory> directoryMap = new HashMap<>();
        Directory rootDir = getRootDirectory(userId);
        log.debug("Root directory: {} (id: {})", rootDir.getName(), rootDir.getId());
        directoryMap.put("", rootDir);

        List<String> sortedPaths = directoryPaths.stream().sorted().toList();
        log.debug("Sorted paths for creation: {}", sortedPaths);

        for (String dirPath : sortedPaths) {
            if (dirPath.isEmpty()) {
                continue;
            }

            String[] parts = dirPath.split("/");
            StringBuilder currentPath = new StringBuilder();

            for (String part : parts) {
                if (!currentPath.isEmpty()) {
                    currentPath.append("/");
                }
                currentPath.append(part);

                String pathKey = currentPath.toString();
                if (!directoryMap.containsKey(pathKey)) {
                    Directory parent = findParentDirectory(currentPath.toString(), part, directoryMap);
                    log.debug("Creating directory '{}' (path: '{}') with parent: {} (parentId: {})",
                        part, pathKey, parent != null ? parent.getName() : "null",
                        parent != null ? parent.getId() : "null");
                    Directory dir = createDirectory(userId, part, parent);
                    directoryMap.put(pathKey, dir);
                    resultBuilder.directoriesCreated++;
                }
            }
        }

        log.info("Created {} directories", resultBuilder.directoriesCreated);
        return directoryMap;
    }

    private Map<Path, ObsidianNote> parseNotes(
        List<Path> markdownFiles,
        Path vaultPath,
        MigrationResultBuilder resultBuilder
    ) {
        log.info("Phase 3: Parsing notes...");

        Map<Path, ObsidianNote> parsedNotes = new HashMap<>();

        for (Path filePath : markdownFiles) {
            try {
                String fileContent = fileReader.readFile(filePath);
                String fileName = filePath.getFileName().toString();
                String relativePath = vaultPath.relativize(filePath).toString();
                LocalDateTime created = fileReader.getFileCreatedTime(filePath);
                LocalDateTime modified = fileReader.getFileModifiedTime(filePath);

                ObsidianNote obsidianNote = noteParser.parse(fileContent, fileName, relativePath, created, modified);
                parsedNotes.put(filePath, obsidianNote);
            } catch (Exception e) {
                log.error("Failed to parse note: {}", filePath, e);
                resultBuilder.addError(filePath.toString(), "Parse error: " + e.getMessage());
            }
        }

        log.info("Parsed {} notes", parsedNotes.size());
        return parsedNotes;
    }

    private Map<Path, Note> createNotes(
        UUID userId,
        Map<Path, ObsidianNote> parsedNotes,
        Map<String, Directory> directoryMap,
        MigrationResultBuilder resultBuilder
    ) {
        log.info("Phase 4: Creating notes in batch...");

        Map<Path, Note> createdNotes = new HashMap<>();
        Map<Path, CreateNoteCommand> noteCommands = new HashMap<>();

        for (Map.Entry<Path, ObsidianNote> entry : parsedNotes.entrySet()) {
            try {
                ObsidianNote obsidianNote = entry.getValue();
                Directory directory = directoryMap.getOrDefault(
                    obsidianNote.getDirectoryPath(),
                    directoryMap.get("")
                );

                CreateNoteCommand command = CreateNoteCommand.builder()
                    .title(obsidianNote.getTitle())
                    .content(obsidianNote.getContent())
                    .dirId(directory.getId())
                    .tags(obsidianNote.getTags())
                    .build();

                noteCommands.put(entry.getKey(), command);
            } catch (Exception e) {
                log.error("Failed to prepare note for batch creation: {}", entry.getKey(), e);
                resultBuilder.addError(entry.getKey().toString(), "Preparation error: " + e.getMessage());
            }
        }

        if (!noteCommands.isEmpty()) {
            try {
                CreateNotesCommand batchCommand = CreateNotesCommand.builder()
                    .notes(new ArrayList<>(noteCommands.values()))
                    .build();

                List<Note> batchCreatedNotes = noteService.createNotes(userId, batchCommand);

                List<Path> paths = new ArrayList<>(noteCommands.keySet());
                for (int i = 0; i < batchCreatedNotes.size(); i++) {
                    createdNotes.put(paths.get(i), batchCreatedNotes.get(i));
                    resultBuilder.notesCreated++;
                }
            } catch (Exception e) {
                log.error("Failed to create notes in batch", e);
                resultBuilder.addError("Batch creation", "Batch creation error: " + e.getMessage());
            }
        }

        log.info("Created {} notes in batch", resultBuilder.notesCreated);
        return createdNotes;
    }

    private Set<String> extractDirectoryPaths(List<Path> markdownFiles, Path vaultPath) {
        return markdownFiles.stream()
            .map(path -> {
                String relativePath = vaultPath.relativize(path).toString();
                int lastSlash = relativePath.lastIndexOf('/');
                return lastSlash == -1 ? "" : relativePath.substring(0, lastSlash);
            })
            .collect(Collectors.toSet());
    }

    private Directory getRootDirectory(UUID userId) {
        List<Directory> rootDirs = directoryService.getRootDirectories(userId);
        if (rootDirs.isEmpty()) {
            return createDirectory(userId, "Root", null);
        }
        return rootDirs.get(0);
    }

    private Directory findParentDirectory(String currentPath, String part, Map<String, Directory> directoryMap) {
        if (currentPath.length() == part.length()) {
            return directoryMap.get("");
        }
        return directoryMap.get(currentPath.substring(0, currentPath.length() - part.length() - 1));
    }

    private Directory createDirectory(UUID userId, String name, Directory parent) {
        CreateDirectoryCommand command = CreateDirectoryCommand.builder()
            .name(name)
            .parentId(parent != null ? parent.getId() : null)
            .build();
        return directoryService.createDirectory(userId, command);
    }

    private Map<UUID, List<UUID>> resolveLinks(
        Map<Path, ObsidianNote> parsedNotes,
        Map<Path, Note> createdNotes,
        MigrationResultBuilder resultBuilder
    ) {
        log.info("Phase 5: Resolving wikilinks...");

        // Build wikilink map: note ID -> list of wikilink references
        Map<UUID, List<WikilinkReference>> wikilinkMap = new HashMap<>();
        for (Map.Entry<Path, ObsidianNote> entry : parsedNotes.entrySet()) {
            Note note = createdNotes.get(entry.getKey());
            if (note != null && !entry.getValue().getWikilinks().isEmpty()) {
                wikilinkMap.put(note.getId(), entry.getValue().getWikilinks());
            }
        }

        log.debug("Collected wikilinks from {} notes", wikilinkMap.size());

        // Build name-to-ID map: note name/alias -> note ID (case-insensitive)
        Map<String, UUID> noteNameToIdMap = new HashMap<>();
        for (Map.Entry<Path, ObsidianNote> entry : parsedNotes.entrySet()) {
            Note note = createdNotes.get(entry.getKey());
            if (note != null) {
                ObsidianNote obsidianNote = entry.getValue();

                // Add title
                noteNameToIdMap.put(obsidianNote.getTitle(), note.getId());

                // Add filename without extension
                String fileNameWithoutExt = obsidianNote.getFileName().replaceAll("\\.md$", "");
                noteNameToIdMap.putIfAbsent(fileNameWithoutExt, note.getId());

                // Add all aliases
                obsidianNote.getAliases().forEach(alias ->
                    noteNameToIdMap.putIfAbsent(alias, note.getId())
                );
            }
        }

        log.debug("Built name-to-ID map with {} entries", noteNameToIdMap.size());

        Map<UUID, List<UUID>> resolvedLinks = linkResolver.resolveLinks(wikilinkMap, noteNameToIdMap);

        int totalLinks = resolvedLinks.values().stream().mapToInt(List::size).sum();
        log.info("Resolved {} wikilinks from {} notes", totalLinks, resolvedLinks.size());

        return resolvedLinks;
    }

    private void createNoteLinks(
        UUID userId,
        Map<UUID, List<UUID>> resolvedLinks,
        MigrationResultBuilder resultBuilder
    ) {
        log.info("Phase 6: Creating note links...");

        int successCount = 0;
        int failCount = 0;
        int skippedCount = 0;

        for (Map.Entry<UUID, List<UUID>> entry : resolvedLinks.entrySet()) {
            UUID sourceId = entry.getKey();
            List<UUID> targetIds = entry.getValue();

            for (UUID targetId : targetIds) {
                try {
                    CreateNoteLinkCommand command = CreateNoteLinkCommand.builder()
                        .sourceNoteId(sourceId)
                        .targetNoteId(targetId)
                        .build();

                    // Check if link already exists before creating
                    if (noteLinkService.linkExists(sourceId, targetId, command.getType(), userId)) {
                        log.debug("Skipping duplicate link from {} to {} (type: {})", sourceId, targetId, command.getType());
                        skippedCount++;
                        continue;
                    }

                    noteLinkService.createLink(userId, command);
                    successCount++;
                    resultBuilder.linksCreated++;
                } catch (Exception e) {
                    log.error("Failed to create link from {} to {}", sourceId, targetId, e);
                    failCount++;
                    resultBuilder.linksFailed++;
                }
            }
        }

        log.info("Created {} note links ({} succeeded, {} skipped, {} failed)",
            successCount + skippedCount + failCount, successCount, skippedCount, failCount);
    }

    private static class MigrationResultBuilder {
        int totalFiles = 0;
        int notesCreated = 0;
        int directoriesCreated = 0;
        int linksCreated = 0;
        int linksFailed = 0;
        List<MigrationResult.MigrationError> errors = new java.util.ArrayList<>();

        void addError(String file, String message) {
            errors.add(MigrationResult.MigrationError.builder()
                .file(file)
                .message(message)
                .build());
        }

        MigrationResult build(long duration) {
            return MigrationResult.builder()
                .totalFiles(totalFiles)
                .notesCreated(notesCreated)
                .directoriesCreated(directoriesCreated)
                .linksCreated(linksCreated)
                .linksFailed(linksFailed)
                .durationMs(duration)
                .errors(errors)
                .build();
        }
    }
}
