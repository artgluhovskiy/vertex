package org.art.vertex.obsidian.domain.service;

import org.art.vertex.obsidian.domain.model.WikilinkReference;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Domain service for resolving Obsidian wikilink references to actual note IDs.
 * <p>
 * This service handles:
 * <ul>
 *   <li>Exact name matching</li>
 *   <li>Case-insensitive matching</li>
 *   <li>Alias resolution</li>
 *   <li>Broken link handling</li>
 * </ul>
 */
public interface ObsidianLinkResolver {

    /**
     * Resolve wikilink references to actual note IDs.
     *
     * @param wikilinks       map of note ID to list of wikilink references
     * @param noteNameToIdMap map of note names/aliases to note IDs (case-insensitive keys)
     * @return map of resolved links: source note ID to list of target note IDs
     */
    Map<UUID, List<UUID>> resolveLinks(
        Map<UUID, List<WikilinkReference>> wikilinks,
        Map<String, UUID> noteNameToIdMap
    );
}
