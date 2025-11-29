package org.art.vertex.obsidian.application.parser;

import java.util.Map;

/**
 * Infrastructure service for extracting YAML frontmatter from Obsidian markdown files.
 * <p>
 * Handles:
 * <ul>
 *   <li>YAML frontmatter parsing</li>
 *   <li>Frontmatter removal from content</li>
 * </ul>
 */
public interface ObsidianMetadataExtractor {

    /**
     * Extract YAML frontmatter from file content.
     *
     * @param fileContent file content with frontmatter
     * @return map of frontmatter key-value pairs, or empty map if no frontmatter
     */
    Map<String, Object> extractFrontmatter(String fileContent);

    /**
     * Remove frontmatter from file content.
     *
     * @param fileContent file content with frontmatter
     * @return content without frontmatter
     */
    String removeFrontmatter(String fileContent);
}
