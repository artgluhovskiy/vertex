package org.art.vertex.obsidian.domain.model;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Value
@Builder
public class ObsidianNote {

    /**
     * Original filename with extension.
     * Example: "My Note.md"
     */
    String fileName;

    /**
     * Relative path from vault root to the file.
     * Example: "folder/subfolder/My Note.md"
     */
    String filePath;

    /**
     * Note title from frontmatter or derived from filename.
     * Priority: frontmatter 'title' field > filename without .md extension
     */
    String title;

    /**
     * Markdown content without YAML frontmatter.
     * This is the actual note content after frontmatter has been removed.
     */
    String content;

    /**
     * Tags extracted from both frontmatter and inline tags.
     * Includes tags from:
     * - Frontmatter 'tags' field
     * - Inline tags like #tag or #parent/child
     */
    Set<String> tags;

    /**
     * Note aliases from frontmatter.
     * Always includes the filename (without .md) as a default alias.
     * Additional aliases can come from frontmatter 'aliases' field.
     */
    Set<String> aliases;

    /**
     * All frontmatter fields as key-value pairs.
     * Preserves original Obsidian metadata for potential reverse migration.
     */
    Map<String, Object> metadata;

    /**
     * Note creation timestamp.
     * Source priority: frontmatter 'created' field > file creation time
     */
    LocalDateTime created;

    /**
     * Note last modification timestamp.
     * Source priority: frontmatter 'modified' field > file modification time
     */
    LocalDateTime modified;

    /**
     * List of wikilink references found in the note content.
     * Used for building the note graph during migration.
     */
    List<WikilinkReference> wikilinks;

    /**
     * Parent directory path relative to vault root.
     * Empty string for notes in vault root.
     * Example: "folder/subfolder" for a note in a subdirectory
     */
    String directoryPath;
}
