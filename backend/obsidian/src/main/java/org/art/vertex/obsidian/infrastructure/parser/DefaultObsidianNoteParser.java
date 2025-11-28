package org.art.vertex.obsidian.infrastructure.parser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.art.vertex.obsidian.domain.model.ObsidianNote;
import org.art.vertex.obsidian.domain.model.WikilinkReference;
import org.art.vertex.obsidian.domain.service.ObsidianNoteParser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Default implementation of ObsidianNoteParser.
 * <p>
 * Parses Obsidian markdown files by:
 * <ul>
 *   <li>Extracting YAML frontmatter</li>
 *   <li>Parsing wikilinks using regex</li>
 *   <li>Extracting inline tags</li>
 *   <li>Building ObsidianNote value object</li>
 * </ul>
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultObsidianNoteParser implements ObsidianNoteParser {

    private final ObsidianMetadataExtractor metadataExtractor;

    private static final Pattern WIKILINK_PATTERN =
        Pattern.compile("(!)?\\[\\[([^\\[\\]|#^]+)(?:\\|([^\\[\\]]+))?(?:#([^\\[\\]^]+))?(?:\\^([^\\[\\]]+))?\\]\\]");

    private static final Pattern INLINE_TAG_PATTERN =
        Pattern.compile("#([a-zA-Z0-9_/-]+)");

    @Override
    public ObsidianNote parse(
        String fileContent,
        String fileName,
        String filePath,
        LocalDateTime fileCreated,
        LocalDateTime fileModified
    ) {
        log.debug("Parsing Obsidian note: {}", fileName);

        Map<String, Object> frontmatter = metadataExtractor.extractFrontmatter(fileContent);
        String content = metadataExtractor.removeFrontmatter(fileContent);

        String title = extractTitle(fileName, frontmatter);
        Set<String> tags = extractTags(content, frontmatter);
        Set<String> aliases = extractAliases(fileName, frontmatter);
        LocalDateTime created = extractTimestamp(frontmatter, "created", fileCreated);
        LocalDateTime modified = extractTimestamp(frontmatter, "modified", fileModified);
        List<WikilinkReference> wikilinks = extractWikilinks(content);
        String directoryPath = extractDirectoryPath(filePath);

        return ObsidianNote.builder()
            .fileName(fileName)
            .filePath(filePath)
            .title(title)
            .content(content)
            .tags(tags)
            .aliases(aliases)
            .metadata(frontmatter)
            .created(created)
            .modified(modified)
            .wikilinks(wikilinks)
            .directoryPath(directoryPath)
            .build();
    }

    private String extractTitle(String fileName, Map<String, Object> frontmatter) {
        if (frontmatter.containsKey("title")) {
            Object titleObj = frontmatter.get("title");
            if (titleObj != null) {
                return titleObj.toString();
            }
        }

        return fileName.replaceAll("\\.md$", "");
    }

    private Set<String> extractTags(String content, Map<String, Object> frontmatter) {
        Set<String> tags = new HashSet<>();

        if (frontmatter.containsKey("tags")) {
            Object tagsObj = frontmatter.get("tags");
            if (tagsObj instanceof List) {
                ((List<?>) tagsObj).forEach(tag -> tags.add(tag.toString()));
            } else if (tagsObj != null) {
                tags.add(tagsObj.toString());
            }
        }

        Matcher matcher = INLINE_TAG_PATTERN.matcher(content);
        while (matcher.find()) {
            tags.add(matcher.group(1));
        }

        return tags;
    }

    private Set<String> extractAliases(String fileName, Map<String, Object> frontmatter) {
        Set<String> aliases = new HashSet<>();

        aliases.add(fileName.replaceAll("\\.md$", ""));

        if (frontmatter.containsKey("aliases")) {
            Object aliasesObj = frontmatter.get("aliases");
            if (aliasesObj instanceof List) {
                ((List<?>) aliasesObj).forEach(alias -> aliases.add(alias.toString()));
            } else if (aliasesObj != null) {
                aliases.add(aliasesObj.toString());
            }
        }

        return aliases;
    }

    private LocalDateTime extractTimestamp(
        Map<String, Object> frontmatter,
        String key,
        LocalDateTime fallback
    ) {
        if (frontmatter.containsKey(key)) {
            Object timestampObj = frontmatter.get(key);
            if (timestampObj instanceof LocalDateTime) {
                return (LocalDateTime) timestampObj;
            } else if (timestampObj instanceof String) {
                try {
                    return LocalDateTime.parse((String) timestampObj);
                } catch (Exception e) {
                    log.warn("Failed to parse timestamp: {}", timestampObj);
                }
            }
        }
        return fallback;
    }

    private List<WikilinkReference> extractWikilinks(String content) {
        List<WikilinkReference> wikilinks = new ArrayList<>();
        Matcher matcher = WIKILINK_PATTERN.matcher(content);

        int lineNumber = 1;
        int lastMatchEnd = 0;

        while (matcher.find()) {
            lineNumber += content.substring(lastMatchEnd, matcher.start()).split("\n").length - 1;
            lastMatchEnd = matcher.end();

            String embedPrefix = matcher.group(1);
            String targetNoteName = matcher.group(2).trim();
            String displayText = matcher.group(3);
            String headingAnchor = matcher.group(4);
            String blockAnchor = matcher.group(5);
            String anchor = headingAnchor != null ? headingAnchor : blockAnchor;
            boolean isEmbedded = "!".equals(embedPrefix);

            wikilinks.add(WikilinkReference.builder()
                .targetNoteName(targetNoteName)
                .displayText(displayText)
                .anchor(anchor)
                .isEmbedded(isEmbedded)
                .lineNumber(lineNumber)
                .build());
        }

        return wikilinks;
    }

    private String extractDirectoryPath(String filePath) {
        int lastSlash = filePath.lastIndexOf('/');
        if (lastSlash == -1) {
            return "";
        }
        return filePath.substring(0, lastSlash);
    }
}
