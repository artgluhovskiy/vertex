package org.art.vertex.infrastructure.note.search.indexing;

import lombok.Builder;
import lombok.Value;

import java.util.Map;

/**
 * Value object representing prepared text for embedding generation.
 * Contains the processed text along with metadata about how it was prepared.
 * <p>
 * This abstraction allows strategies to provide additional context about the indexing process:
 * - What fields were included and their weights
 * - Whether chunking was applied
 * - What transformations were performed
 * - Estimated token count
 * <p>
 * The metadata is useful for:
 * - Debugging search quality issues
 * - Analyzing which strategies work best
 * - Monitoring text length vs. embedding model limits
 * - Optimizing weighting strategies
 * <p>
 * Example usage:
 * <pre>
 * IndexableText indexableText = IndexableText.builder()
 *     .text("Machine Learning Machine Learning Machine Learning Neural Networks...")
 *     .putMetadata("title_weight", 3)
 *     .putMetadata("strategy", "BASIC")
 *     .putMetadata("length", 1234)
 *     .build();
 * </pre>
 */
@Value
@Builder(toBuilder = true)
public class IndexableText {

    /**
     * The prepared text ready for embedding generation.
     * Should be within the embedding model's max token limit.
     */
    String text;

    /**
     * Metadata about how the text was prepared.
     * Common keys:
     * - "strategy": Strategy name (e.g., "BASIC", "ENHANCED")
     * - "title_weight": How many times title was repeated
     * - "summary_weight": How many times summary was repeated
     * - "tag_weight": How many times each tag was repeated
     * - "length": Character length of the text
     * - "estimated_tokens": Estimated token count
     * - "truncated": Whether text was truncated (true/false)
     * - "chunked": Whether text was chunked (true/false)
     * - "chunk_index": If chunked, the chunk index
     */
    @Builder.Default
    Map<String, Object> metadata = Map.of();

    /**
     * Get metadata value by key.
     *
     * @param key Metadata key
     * @return Metadata value or null if not present
     */
    public Object getMetadata(String key) {
        return metadata.get(key);
    }

    /**
     * Get metadata value by key with type casting.
     *
     * @param key  Metadata key
     * @param type Expected value type
     * @param <T>  Value type
     * @return Metadata value or null if not present
     * @throws ClassCastException if value cannot be cast to expected type
     */
    @SuppressWarnings("unchecked")
    public <T> T getMetadata(String key, Class<T> type) {
        Object value = metadata.get(key);
        return value != null ? (T) value : null;
    }

    /**
     * Get character length of the prepared text.
     *
     * @return Text length in characters
     */
    public int getLength() {
        return text != null ? text.length() : 0;
    }

    /**
     * Estimate token count (rough approximation: 1 token H 4 characters).
     * Actual token count depends on the tokenizer used by the embedding model.
     *
     * @return Estimated token count
     */
    public int getEstimatedTokenCount() {
        return getLength() / 4;
    }

    /**
     * Check if text is empty.
     *
     * @return true if text is null or empty
     */
    public boolean isEmpty() {
        return text == null || text.isEmpty();
    }

    /**
     * Check if text was truncated during preparation.
     *
     * @return true if metadata indicates truncation
     */
    public boolean isTruncated() {
        Boolean truncated = getMetadata("truncated", Boolean.class);
        return Boolean.TRUE.equals(truncated);
    }

    /**
     * Check if text represents a chunk from a longer note.
     *
     * @return true if metadata indicates chunking
     */
    public boolean isChunked() {
        Boolean chunked = getMetadata("chunked", Boolean.class);
        return Boolean.TRUE.equals(chunked);
    }

    /**
     * Get chunk index if this is a chunked text.
     *
     * @return Chunk index or null if not chunked
     */
    public Integer getChunkIndex() {
        return getMetadata("chunk_index", Integer.class);
    }

    /**
     * Create simple indexable text without metadata.
     *
     * @param text Prepared text
     * @return IndexableText instance
     */
    public static IndexableText of(String text) {
        return IndexableText.builder()
            .text(text)
            .build();
    }

    /**
     * Create indexable text with strategy metadata.
     *
     * @param text         Prepared text
     * @param strategyName Strategy name
     * @return IndexableText instance
     */
    public static IndexableText of(String text, String strategyName) {
        return IndexableText.builder()
            .text(text)
            .metadata(Map.of(
                "strategy", strategyName,
                "length", text != null ? text.length() : 0
            ))
            .build();
    }
}
