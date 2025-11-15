package org.art.vertex.domain.note;

import org.art.vertex.domain.note.model.NoteEmbedding;
import org.art.vertex.domain.note.search.model.SearchHit;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for NoteEmbedding aggregate.
 * Follows DDD repository pattern for persistence operations.
 */
public interface NoteEmbeddingRepository {

    /**
     * Save a new embedding.
     *
     * @param embedding NoteEmbedding to save
     * @return Saved embedding
     */
    NoteEmbedding save(NoteEmbedding embedding);

    /**
     * Update an existing embedding.
     *
     * @param embedding NoteEmbedding to update
     * @return Updated embedding
     */
    NoteEmbedding update(NoteEmbedding embedding);

    /**
     * Find embedding by note ID (full-note embedding only).
     *
     * @param noteId Note ID
     * @return Optional embedding
     */
    Optional<NoteEmbedding> findByNoteId(UUID noteId);

    /**
     * Get embedding by note ID (throws if not found).
     *
     * @param noteId Note ID
     * @return NoteEmbedding
     */
    NoteEmbedding getByNoteId(UUID noteId);

    /**
     * Find all embeddings for a note (including chunks).
     *
     * @param noteId Note ID
     * @return List of embeddings
     */
    List<NoteEmbedding> findAllByNoteId(UUID noteId);

    /**
     * Check if embedding exists for a note.
     *
     * @param noteId Note ID
     * @return true if embedding exists
     */
    boolean existsByNoteId(UUID noteId);

    /**
     * Delete all embeddings for a note.
     *
     * @param noteId Note ID
     */
    void deleteByNoteId(UUID noteId);

    /**
     * Search for notes using vector similarity.
     *
     * @param queryVector   Query embedding vector
     * @param model         Model name to filter by
     * @param userId        User ID to filter results
     * @param minSimilarity Minimum similarity threshold (0.0-1.0)
     * @param limit         Maximum number of results
     * @return List of search hits ordered by similarity
     */
    List<SearchHit> searchByVector(
        List<Float> queryVector,
        String model,
        UUID userId,
        double minSimilarity,
        int limit
    );

    /**
     * Find k-nearest neighbor notes.
     *
     * @param noteId        Note ID to find neighbors for
     * @param userId        User ID to filter results
     * @param minSimilarity Minimum similarity threshold
     * @param k             Number of neighbors to return
     * @return List of k most similar note IDs
     */
    List<UUID> findKNearestNoteIds(
        UUID noteId,
        UUID userId,
        double minSimilarity,
        int k
    );
}
