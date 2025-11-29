package org.art.vertex.obsidian.application.parser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Default implementation of ObsidianMetadataExtractor using SnakeYAML.
 * <p>
 * Parses YAML frontmatter in the format:
 * <pre>
 * ---
 * title: "Note Title"
 * tags: [tag1, tag2]
 * created: 2024-01-15
 * ---
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultObsidianMetadataExtractor implements ObsidianMetadataExtractor {

    private static final Pattern FRONTMATTER_PATTERN =
        Pattern.compile("^---\\s*\\n(.*?)\\n---\\s*\\n", Pattern.DOTALL);

    private final Yaml yaml;

    @Override
    public Map<String, Object> extractFrontmatter(String fileContent) {
        if (fileContent == null || fileContent.trim().isEmpty()) {
            return new HashMap<>();
        }

        Matcher matcher = FRONTMATTER_PATTERN.matcher(fileContent);
        if (!matcher.find()) {
            return new HashMap<>();
        }

        String frontmatterContent = matcher.group(1);

        try {
            Map<String, Object> metadata = yaml.load(frontmatterContent);
            return metadata != null ? metadata : new HashMap<>();
        } catch (Exception e) {
            log.warn("Failed to parse YAML frontmatter. Content will be processed without metadata.", e);
            return new HashMap<>();
        }
    }

    @Override
    public String removeFrontmatter(String fileContent) {
        if (fileContent == null) {
            return "";
        }

        Matcher matcher = FRONTMATTER_PATTERN.matcher(fileContent);
        if (matcher.find()) {
            return fileContent.substring(matcher.end());
        }

        return fileContent;
    }
}
