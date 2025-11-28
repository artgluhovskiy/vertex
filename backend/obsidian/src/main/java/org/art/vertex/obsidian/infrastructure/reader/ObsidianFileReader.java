package org.art.vertex.obsidian.infrastructure.reader;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Infrastructure service for reading Obsidian vault files from the filesystem.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Discover all markdown files in vault</li>
 *   <li>Read file contents</li>
 *   <li>Extract file metadata (timestamps)</li>
 * </ul>
 */
public interface ObsidianFileReader {

    /**
     * Discover all markdown files in vault directory.
     *
     * @param vaultPath path to Obsidian vault root
     * @return list of markdown file paths
     */
    List<Path> discoverMarkdownFiles(Path vaultPath);

    /**
     * Read file content.
     *
     * @param filePath path to markdown file
     * @return file content as string
     */
    String readFile(Path filePath);

    /**
     * Get file creation timestamp.
     *
     * @param filePath path to file
     * @return creation timestamp
     */
    LocalDateTime getFileCreatedTime(Path filePath);

    /**
     * Get file modification timestamp.
     *
     * @param filePath path to file
     * @return modification timestamp
     */
    LocalDateTime getFileModifiedTime(Path filePath);
}
