package org.art.vertex.obsidian.domain.service;

import org.art.vertex.obsidian.domain.model.ObsidianNote;

import java.time.LocalDateTime;

/**
 * Domain service for parsing Obsidian markdown files into structured ObsidianNote objects.
 * <p>
 * This service handles:
 * <ul>
 *   <li>YAML frontmatter extraction</li>
 *   <li>Wikilink reference detection</li>
 *   <li>Tag extraction (frontmatter and inline)</li>
 *   <li>Metadata preservation</li>
 * </ul>
 */
public interface ObsidianNoteParser {

    /**
     * Parse Obsidian markdown file into ObsidianNote value object.
     *
     * @param fileContent   raw file content (including frontmatter)
     * @param fileName      file name (e.g., "My Note.md")
     * @param filePath      relative path from vault root
     * @param fileCreated   file creation timestamp
     * @param fileModified  file modification timestamp
     * @return parsed ObsidianNote
     */
    ObsidianNote parse(
        String fileContent,
        String fileName,
        String filePath,
        LocalDateTime fileCreated,
        LocalDateTime fileModified
    );
}
