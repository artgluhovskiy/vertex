package org.art.vertex.infrastructure.note.search.jpa.projection;

import java.util.UUID;

/**
 * Projection interface for vector search query results.
 * Used by Spring Data JPA to efficiently map native query results
 * without loading full entities.
 * <p>
 * This projection includes note metadata and similarity score from pgvector.
 */
public interface VectorSearchResultProjection {

    /**
     * Get note ID.
     *
     * @return note UUID
     */
    UUID getNoteId();

    /**
     * Get user ID who owns this note.
     *
     * @return user UUID
     */
    UUID getUserId();

    /**
     * Get note title.
     *
     * @return note title
     */
    String getTitle();

    /**
     * Get note content.
     *
     * @return note content
     */
    String getContent();

    /**
     * Get note summary.
     *
     * @return note summary
     */
    String getSummary();

    /**
     * Get similarity score from vector search.
     * Score is calculated as: 1 - cosine_distance
     * Range: 0.0 (completely different) to 1.0 (identical)
     *
     * @return similarity score (0.0-1.0)
     */
    Double getSimilarity();

    /**
     * Get chunk index if this is a chunked embedding.
     * NULL indicates full-note embedding.
     *
     * @return chunk index or null
     */
    Integer getChunkIndex();
}
