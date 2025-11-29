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

                UUID targetId = resolveWikilink(ref.getTargetNoteName(), noteNameToIdMap);
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

    private UUID resolveWikilink(String targetNoteName, Map<String, UUID> noteNameToIdMap) {
        UUID id = noteNameToIdMap.get(targetNoteName);
        if (id != null) {
            return id;
        }

        String lowerTarget = targetNoteName.toLowerCase();
        for (Map.Entry<String, UUID> entry : noteNameToIdMap.entrySet()) {
            if (entry.getKey().toLowerCase().equals(lowerTarget)) {
                return entry.getValue();
            }
        }

        String withoutExtension = targetNoteName.replaceAll("\\.md$", "");
        id = noteNameToIdMap.get(withoutExtension);
        if (id != null) {
            return id;
        }

        String lowerWithoutExtension = withoutExtension.toLowerCase();
        for (Map.Entry<String, UUID> entry : noteNameToIdMap.entrySet()) {
            String keyWithoutExt = entry.getKey().replaceAll("\\.md$", "");
            if (keyWithoutExt.toLowerCase().equals(lowerWithoutExtension)) {
                return entry.getValue();
            }
        }

        return null;
    }
}
