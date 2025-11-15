package org.art.vertex.infrastructure.note.search.indexing;

import lombok.extern.slf4j.Slf4j;
import org.art.vertex.domain.note.model.Note;
import org.art.vertex.domain.tag.model.Tag;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Basic text indexing strategy with simple field weighting.
 * <p>
 * Strategy:
 * - Title: 3x weight (repeated 3 times)
 * - Content: 1x weight (as-is)
 * - Tags: 1x weight (each tag once)
 * <p>
 * This strategy is:
 * - Fast and simple
 * - Suitable for general-purpose semantic search
 * - Good for notes with clear titles and concise content
 * - Does not handle very long notes optimally
 * <p>
 * Text format:
 * <pre>
 * [Title] [Title] [Title]
 * [Content]
 * [Tag1] [Tag2] [Tag3]
 * </pre>
 * <p>
 * Example:
 * <pre>
 * Machine Learning Machine Learning Machine Learning
 * Neural networks are a subset of machine learning...
 * AI ML DeepLearning
 * </pre>
 * <p>
 * Configuration:
 * - Registered as bean in NoteInfrastructureConfig
 * - Enabled by default
 * - Use for notes < 8k tokens
 */
@Slf4j
public class BasicTextIndexingStrategy implements TextIndexingStrategy {

    private static final String STRATEGY_NAME = "BASIC";
    private static final int TITLE_WEIGHT = 3;
    private static final int MAX_TEXT_LENGTH = 32000; // ~8k tokens

    @Override
    public IndexableText prepareText(Note note) {
        if (note == null) {
            throw new IllegalArgumentException("Note cannot be null");
        }

        log.debug("Preparing text for indexing: noteId={}, strategy={}", note.getId(), STRATEGY_NAME);

        StringBuilder textBuilder = new StringBuilder();
        Map<String, Object> metadata = new HashMap<>();

        // Add weighted title (3x)
        if (note.getTitle() != null && !note.getTitle().isBlank()) {
            String title = note.getTitle().trim();
            for (int i = 0; i < TITLE_WEIGHT; i++) {
                textBuilder.append(title).append(" ");
            }
            metadata.put("title_weight", TITLE_WEIGHT);
            metadata.put("title_included", true);
        }

        // Add content (1x)
        if (note.getContent() != null && !note.getContent().isBlank()) {
            textBuilder.append("\n").append(note.getContent().trim());
            metadata.put("content_included", true);
        }

        // Add tags (1x each)
        if (note.getTags() != null && !note.getTags().isEmpty()) {
            String tagsText = note.getTags().stream()
                .map(Tag::getName)
                .filter(name -> name != null && !name.isBlank())
                .collect(Collectors.joining(" "));

            if (!tagsText.isEmpty()) {
                textBuilder.append("\n").append(tagsText);
                metadata.put("tags_included", true);
                metadata.put("tags_count", note.getTags().size());
            }
        }

        String finalText = textBuilder.toString().trim();

        // Handle truncation if text is too long
        boolean truncated = false;
        if (finalText.length() > MAX_TEXT_LENGTH) {
            log.warn("Text too long for note {}: {} chars, truncating to {}",
                note.getId(), finalText.length(), MAX_TEXT_LENGTH);
            finalText = finalText.substring(0, MAX_TEXT_LENGTH);
            truncated = true;
        }

        // Add metadata
        metadata.put("strategy", STRATEGY_NAME);
        metadata.put("length", finalText.length());
        metadata.put("estimated_tokens", finalText.length() / 4);
        metadata.put("truncated", truncated);
        metadata.put("chunked", false);

        log.trace("Prepared text for indexing: noteId={}, length={}, truncated={}",
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
        return String.format("Basic text indexing strategy with title weight=%d", TITLE_WEIGHT);
    }
}
