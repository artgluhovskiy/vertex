package org.art.vertex.domain.note.enrichment;

import org.art.vertex.domain.note.model.Note;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Domain service containing business logic for note enrichment.
 */
public class NoteEnrichmentService {

    private static final Pattern WIKILINK_PATTERN = Pattern.compile("\\[\\[([^\\]]+)\\]\\]");

    /**
     * Extract wikilinks from note content.
     * This is pure domain logic - no infrastructure dependencies.
     */
    public List<String> extractWikiLinks(Note note) {
        if (note.getContent() == null || note.getContent().isEmpty()) {
            return List.of();
        }

        Matcher matcher = WIKILINK_PATTERN.matcher(note.getContent());
        return matcher.results()
            .map(result -> result.group(1).trim())
            .distinct()
            .collect(Collectors.toList());
    }

    /**
     * Determine if a note should be auto-enriched based on business rules.
     */
    public boolean shouldAutoEnrich(Note note) {
        // Business rule: auto-enrich if note has substantial content
        return note.getContent() != null && note.getContent().length() > 100;
    }

    /**
     * Calculate similarity score between notes based on domain logic.
     */
    public double calculateNoteSimilarity(Note note1, Note note2) {
        // Business logic for similarity calculation
        // Could be based on tags, directory, title similarity, etc.

        double score = 0.0;

        // Same directory adds to similarity
        if (note1.getDir() != null &&
            note1.getDir().equals(note2.getDir())) {
            score += 0.3;
        }

        // Shared tags increase similarity
        if (note1.getTags() != null && note2.getTags() != null) {
            long sharedTags = note1.getTags().stream()
                .filter(note2.getTags()::contains)
                .count();
            score += Math.min(0.5, sharedTags * 0.1);
        }

        return Math.min(1.0, score);
    }
}