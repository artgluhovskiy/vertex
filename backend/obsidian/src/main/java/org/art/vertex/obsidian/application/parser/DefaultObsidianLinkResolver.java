package org.art.vertex.obsidian.application.parser;

import lombok.extern.slf4j.Slf4j;
import org.art.vertex.obsidian.domain.model.WikilinkReference;
import org.art.vertex.obsidian.domain.service.ObsidianLinkResolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Default implementation of ObsidianLinkResolver.
 * <p>
 * Resolves wikilinks using multiple strategies:
 * <ul>
 *   <li>Exact match (case-sensitive)</li>
 *   <li>Case-insensitive match</li>
 *   <li>Match without .md extension</li>
 * </ul>
 */
@Slf4j
public class DefaultObsidianLinkResolver implements ObsidianLinkResolver {

    @Override
    public Map<UUID, List<UUID>> resolveLinks(
        Map<UUID, List<WikilinkReference>> wikilinks,
        Map<String, UUID> noteNameToIdMap
    ) {
        log.debug("Resolving links for {} notes", wikilinks.size());

        // Build case-insensitive lookup map once - O(n)
        Map<String, UUID> caseInsensitiveMap = buildCaseInsensitiveMap(noteNameToIdMap);

        Map<UUID, List<UUID>> resolvedLinks = new HashMap<>();

        for (Map.Entry<UUID, List<WikilinkReference>> entry : wikilinks.entrySet()) {
            UUID sourceNoteId = entry.getKey();
            List<WikilinkReference> references = entry.getValue();
            List<UUID> targetIds = new ArrayList<>();

            for (WikilinkReference ref : references) {
                if (ref.isEmbedded()) {
                    log.debug("Skipping embedded reference: {}", ref.getTargetNoteName());
                    continue;
                }

                UUID targetId = resolveWikilink(ref.getTargetNoteName(), caseInsensitiveMap);
                if (targetId != null) {
                    targetIds.add(targetId);
                } else {
                    log.warn("Could not resolve wikilink: {} (line {})",
                        ref.getTargetNoteName(), ref.getLineNumber());
                }
            }

            if (!targetIds.isEmpty()) {
                resolvedLinks.put(sourceNoteId, targetIds);
            }
        }

        log.info("Resolved {} links from {} notes",
            resolvedLinks.values().stream().mapToInt(List::size).sum(),
            resolvedLinks.size());

        return resolvedLinks;
    }

    private Map<String, UUID> buildCaseInsensitiveMap(Map<String, UUID> noteNameToIdMap) {
        Map<String, UUID> caseInsensitive = new HashMap<>();

        for (Map.Entry<String, UUID> entry : noteNameToIdMap.entrySet()) {
            String key = entry.getKey();
            String lowerKey = key.toLowerCase();
            UUID value = entry.getValue();

            // Store original key
            caseInsensitive.put(key, value);

            // Store lowercase version (only if not already present to preserve exact matches)
            caseInsensitive.putIfAbsent(lowerKey, value);

            // Store without .md extension
            String withoutExt = key.replaceAll("\\.md$", "");
            caseInsensitive.putIfAbsent(withoutExt, value);
            caseInsensitive.putIfAbsent(withoutExt.toLowerCase(), value);
        }

        return caseInsensitive;
    }

    private UUID resolveWikilink(String targetNoteName, Map<String, UUID> caseInsensitiveMap) {
        // Direct O(1) lookup
        UUID id = caseInsensitiveMap.get(targetNoteName);
        if (id != null) {
            return id;
        }

        // Try lowercase version
        return caseInsensitiveMap.get(targetNoteName.toLowerCase());
    }
}
