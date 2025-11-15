package org.art.vertex.infrastructure.note.search.indexing;

import lombok.extern.slf4j.Slf4j;
import org.art.vertex.domain.note.model.Note;
import org.art.vertex.domain.tag.model.Tag;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Enhanced text indexing strategy with higher field weighting and metadata inclusion.
 * <p>
 * Strategy:
 * - Title: 5x weight (repeated 5 times)
 * - Summary: 3x weight (repeated 3 times, if present)
 * - Content: 1x weight (as-is)
 * - Tags: 3x weight (each tag repeated 3 times)
 * - Metadata: Included as key-value pairs
 * <p>
 * This strategy is:
 * - More aggressive with field weighting
 * - Suitable for notes with rich metadata
 * - Better for semantic search when title/summary are highly relevant
 * - Includes all available note information
 * <p>
 * Text format:
 * <pre>
 * [Title] [Title] [Title] [Title] [Title]
 * [Summary] [Summary] [Summary]
 * [Content]
 * [Tag1] [Tag1] [Tag1] [Tag2] [Tag2] [Tag2]
 * [MetadataKey1: MetadataValue1]
 * </pre>
 * <p>
 * Example:
 * <pre>
 * Machine Learning Machine Learning Machine Learning Machine Learning Machine Learning
 * Introduction to neural networks Introduction to neural networks Introduction to neural networks
 * Neural networks are a subset of machine learning...
 * AI AI AI ML ML ML DeepLearning DeepLearning DeepLearning
 * category: Technology, difficulty: Advanced
 * </pre>
 * <p>
 * Configuration:
 * - Registered conditionally in NoteInfrastructureConfig
 * - Controlled by: embedding.indexing.enhanced.enabled=true (default: false)
 * - Use for notes with rich metadata and clear structure
 */
@Slf4j
public class EnhancedTextIndexingStrategy implements TextIndexingStrategy {

    private static final String STRATEGY_NAME = "ENHANCED";
    private static final int TITLE_WEIGHT = 5;
    private static final int SUMMARY_WEIGHT = 3;
    private static final int TAG_WEIGHT = 3;
    private static final int MAX_TEXT_LENGTH = 32000; // ~8k tokens

    @Override
    public IndexableText prepareText(Note note) {
        if (note == null) {
            throw new IllegalArgumentException("Note cannot be null");
        }

        log.debug("Preparing enhanced text for indexing: noteId={}, strategy={}", note.getId(), STRATEGY_NAME);

        StringBuilder textBuilder = new StringBuilder();
        Map<String, Object> metadata = new HashMap<>();

        // Add weighted title (5x)
        if (note.getTitle() != null && !note.getTitle().isBlank()) {
            String title = note.getTitle().trim();
            for (int i = 0; i < TITLE_WEIGHT; i++) {
                textBuilder.append(title).append(" ");
            }
            metadata.put("title_weight", TITLE_WEIGHT);
            metadata.put("title_included", true);
        }

        // Add weighted summary (3x)
        if (note.getSummary() != null && !note.getSummary().isBlank()) {
            textBuilder.append("\n");
            String summary = note.getSummary().trim();
            for (int i = 0; i < SUMMARY_WEIGHT; i++) {
                textBuilder.append(summary).append(" ");
            }
            metadata.put("summary_weight", SUMMARY_WEIGHT);
            metadata.put("summary_included", true);
        }

        // Add content (1x)
        if (note.getContent() != null && !note.getContent().isBlank()) {
            textBuilder.append("\n").append(note.getContent().trim());
            metadata.put("content_included", true);
        }

        // Add weighted tags (3x each)
        if (note.getTags() != null && !note.getTags().isEmpty()) {
            textBuilder.append("\n");
            for (Tag tag : note.getTags()) {
                String tagName = tag.getName();
                if (tagName != null && !tagName.isBlank()) {
                    for (int i = 0; i < TAG_WEIGHT; i++) {
                        textBuilder.append(tagName.trim()).append(" ");
                    }
                }
            }
            metadata.put("tags_weight", TAG_WEIGHT);
            metadata.put("tags_included", true);
            metadata.put("tags_count", note.getTags().size());
        }

        // Add metadata as key-value pairs
        if (note.getMetadata() != null && !note.getMetadata().isEmpty()) {
            String metadataText = note.getMetadata().entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));

            if (!metadataText.isEmpty()) {
                textBuilder.append("\n").append(metadataText);
                metadata.put("note_metadata_included", true);
                metadata.put("note_metadata_count", note.getMetadata().size());
            }
        }

        String finalText = textBuilder.toString().trim();

        // Handle truncation if text is too long
        boolean truncated = false;
        if (finalText.length() > MAX_TEXT_LENGTH) {
            log.warn("Enhanced text too long for note {}: {} chars, truncating to {}",
                note.getId(), finalText.length(), MAX_TEXT_LENGTH);
            finalText = finalText.substring(0, MAX_TEXT_LENGTH);
            truncated = true;
        }

        // Add indexing metadata
        metadata.put("strategy", STRATEGY_NAME);
        metadata.put("length", finalText.length());
        metadata.put("estimated_tokens", finalText.length() / 4);
        metadata.put("truncated", truncated);
        metadata.put("chunked", false);

        log.trace("Prepared enhanced text for indexing: noteId={}, length={}, truncated={}",
            note.getId(), finalText.length(), truncated);

        return IndexableText.builder()
            .text(finalText)
            .metadata(metadata)
            .build();
    }

    @Override
    public String getStrategyName() {
        return STRATEGY_NAME;
    }

    @Override
    public int getEstimatedMaxTextLength() {
        return MAX_TEXT_LENGTH;
    }

    @Override
    public boolean supportsChunking() {
        return false;
    }

    @Override
    public String getDescription() {
        return String.format(
            "Enhanced text indexing strategy with title=%dx, summary=%dx, tags=%dx weights",
            TITLE_WEIGHT, SUMMARY_WEIGHT, TAG_WEIGHT
        );
    }
}
