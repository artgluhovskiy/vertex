package org.art.vertex.infrastructure.note.search.indexing;

import org.art.vertex.domain.note.model.Note;

/**
 * Strategy interface for converting notes into indexable text for embedding generation.
 * Different strategies can emphasize different aspects of notes (title, content, metadata)
 * with different weights to optimize semantic search quality.
 * <p>
 * Implementations should consider:
 * - **Text weighting**: More important fields (title, summary) should appear multiple times
 * - **Length constraints**: Embedding models have max token limits (~8k tokens typically)
 * - **RAG optimization**: For long notes, consider chunking strategies
 * - **Metadata inclusion**: Tags, categories, and other metadata can improve search relevance
 * <p>
 * Common strategies:
 * - **Basic**: Simple concatenation of title + content + tags
 * - **Enhanced**: Weighted fields with title (5x), summary (3x), content, tags (3x each)
 * - **Chunked**: Split long notes into smaller chunks for better RAG performance
 * <p>
 * Example usage:
 * <pre>
 * TextIndexingStrategy strategy = new BasicTextIndexingStrategy();
 * IndexableText indexableText = strategy.prepareText(note);
 * List&lt;Float&gt; embedding = embeddingProvider.embed(indexableText.getText());
 * </pre>
 *
 * @see IndexableText
 * @see org.art.vertex.infrastructure.note.search.embedding.EmbeddingProvider
 */
public interface TextIndexingStrategy {

    /**
     * Prepare note for indexing by converting it into indexable text.
     * The returned text will be used to generate embeddings.
     * <p>
     * Implementations should:
     * - Apply appropriate field weighting
     * - Respect embedding model's max text length
     * - Handle null/empty fields gracefully
     * - Consider RAG optimization for long notes
     *
     * @param note Note to prepare for indexing
     * @return Indexable text representation
     * @throws IllegalArgumentException if note is null
     */
    IndexableText prepareText(Note note);

    /**
     * Create searchable text from note for full-text indexing.
     * Legacy method for backward compatibility with full-text search.
     *
     * @param note Note to create index text from
     * @return Searchable text representation
     * @deprecated Use {@link #prepareText(Note)} for semantic search
     */
    @Deprecated
    default String createIndexText(Note note) {
        return prepareText(note).getText();
    }

    /**
     * Get the strategy name for logging and debugging.
     *
     * @return Strategy name (e.g., "BASIC", "ENHANCED", "CHUNKED")
     */
    String getStrategyName();

    /**
     * Get estimated max text length this strategy produces.
     * Helps select appropriate embedding models.
     *
     * @return Estimated max text length in characters
     */
    default int getEstimatedMaxTextLength() {
        return 8000; // ~2000 tokens for most models
    }

    /**
     * Check if this strategy supports chunking.
     * Chunked strategies split long notes into multiple embeddings.
     *
     * @return true if strategy supports chunking, false otherwise
     */
    default boolean supportsChunking() {
        return false;
    }

    /**
     * Get description of this strategy for documentation/debugging.
     *
     * @return Human-readable strategy description
     */
    default String getDescription() {
        return "Text indexing strategy: " + getStrategyName();
    }
}
