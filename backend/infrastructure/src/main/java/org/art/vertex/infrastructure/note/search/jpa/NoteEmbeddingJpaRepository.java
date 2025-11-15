package org.art.vertex.infrastructure.note.search.jpa;

import org.art.vertex.infrastructure.note.search.entity.NoteEmbeddingEntity;
import org.art.vertex.infrastructure.note.search.jpa.projection.VectorSearchResultProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA repository for note embeddings with pgvector support.
 * Provides native SQL queries for vector similarity search using cosine distance.
 * <p>
 * Query performance considerations:
 * - HNSW indexes provide approximate nearest neighbor search (fast, high recall)
 * - Cosine distance operator: <=> (optimal for normalized vectors)
 * - Similarity score: 1 - cosine_distance (range: 0.0 to 1.0)
 * <p>
 * For query tuning, use: SET hnsw.ef_search = N (default: 40, higher = better recall)
 */
@Repository
public interface NoteEmbeddingJpaRepository extends JpaRepository<NoteEmbeddingEntity, UUID> {

    // ========================================================================
    // Vector Similarity Search Query (unified for all dimensions)
    // ========================================================================

    /**
     * Search for similar notes using specified dimension embedding vector.
     * Uses pgvector cosine distance with HNSW index for fast approximate search.
     * Dynamically selects the correct embedding column based on dimension parameter.
     *
     * @param queryVector    Query embedding as pgvector string format "[0.1,0.2,...]"
     * @param dimension      Embedding dimension (768=SMALL, 1024=MEDIUM, 1536=LARGE)
     * @param model          Embedding model name to filter by
     * @param userId         User ID to filter results (security isolation)
     * @param minSimilarity  Minimum similarity threshold (0.0-1.0)
     * @param limit          Maximum number of results
     * @return List of search result projections ordered by similarity (highest first)
     */
    @Query(value = """
        SELECT
            n.id as noteId,
            n.user_id as userId,
            n.title,
            n.content,
            n.summary,
            CASE
                WHEN :dimension = 768 THEN (1 - (e.embedding_small <=> CAST(:queryVector AS vector)))
                WHEN :dimension = 1024 THEN (1 - (e.embedding_medium <=> CAST(:queryVector AS vector)))
                WHEN :dimension = 1536 THEN (1 - (e.embedding_large <=> CAST(:queryVector AS vector)))
                ELSE 0.0
            END as similarity,
            e.chunk_index as chunkIndex
        FROM note_embeddings e
        INNER JOIN notes n ON e.note_id = n.id
        WHERE n.user_id = :userId
          AND e.model = :model
          AND e.dimension = :dimension
          AND CASE
                WHEN :dimension = 768 THEN e.embedding_small IS NOT NULL
                WHEN :dimension = 1024 THEN e.embedding_medium IS NOT NULL
                WHEN :dimension = 1536 THEN e.embedding_large IS NOT NULL
                ELSE false
              END
          AND CASE
                WHEN :dimension = 768 THEN (1 - (e.embedding_small <=> CAST(:queryVector AS vector))) >= :minSimilarity
                WHEN :dimension = 1024 THEN (1 - (e.embedding_medium <=> CAST(:queryVector AS vector))) >= :minSimilarity
                WHEN :dimension = 1536 THEN (1 - (e.embedding_large <=> CAST(:queryVector AS vector))) >= :minSimilarity
                ELSE false
              END
        ORDER BY
            CASE
                WHEN :dimension = 768 THEN e.embedding_small <=> CAST(:queryVector AS vector)
                WHEN :dimension = 1024 THEN e.embedding_medium <=> CAST(:queryVector AS vector)
                WHEN :dimension = 1536 THEN e.embedding_large <=> CAST(:queryVector AS vector)
                ELSE 1.0
            END
        LIMIT :limit
        """, nativeQuery = true)
    List<VectorSearchResultProjection> searchByVector(
        @Param("queryVector") String queryVector,
        @Param("dimension") int dimension,
        @Param("model") String model,
        @Param("userId") UUID userId,
        @Param("minSimilarity") double minSimilarity,
        @Param("limit") int limit
    );

    // ========================================================================
    // K-Nearest Neighbors Query (unified for all dimensions)
    // ========================================================================

    /**
     * Find k-nearest neighbors for a given note using specified dimension.
     * Compares the note's embedding against all other notes' embeddings.
     * Excludes the query note itself and applies similarity threshold.
     * Dynamically selects the correct embedding column based on dimension parameter.
     *
     * @param noteId        Note ID to find neighbors for
     * @param dimension     Embedding dimension (768=SMALL, 1024=MEDIUM, 1536=LARGE)
     * @param userId        User ID to filter results (security isolation)
     * @param minSimilarity Minimum similarity threshold (0.0-1.0)
     * @param k             Number of nearest neighbors to return
     * @return List of k most similar notes ordered by similarity (highest first)
     */
    @Query(value = """
        SELECT
            n.id as noteId,
            n.user_id as userId,
            n.title,
            n.content,
            n.summary,
            CASE
                WHEN :dimension = 768 THEN (1 - (e.embedding_small <=> e_target.embedding_small))
                WHEN :dimension = 1024 THEN (1 - (e.embedding_medium <=> e_target.embedding_medium))
                WHEN :dimension = 1536 THEN (1 - (e.embedding_large <=> e_target.embedding_large))
                ELSE 0.0
            END as similarity,
            e.chunk_index as chunkIndex
        FROM note_embeddings e
        INNER JOIN notes n ON e.note_id = n.id
        INNER JOIN note_embeddings e_target ON e_target.note_id = :noteId
        WHERE n.user_id = :userId
          AND e.note_id != :noteId
          AND e.dimension = :dimension
          AND e_target.dimension = :dimension
          AND e.chunk_index IS NULL
          AND e_target.chunk_index IS NULL
          AND CASE
                WHEN :dimension = 768 THEN e.embedding_small IS NOT NULL AND e_target.embedding_small IS NOT NULL
                WHEN :dimension = 1024 THEN e.embedding_medium IS NOT NULL AND e_target.embedding_medium IS NOT NULL
                WHEN :dimension = 1536 THEN e.embedding_large IS NOT NULL AND e_target.embedding_large IS NOT NULL
                ELSE false
              END
          AND CASE
                WHEN :dimension = 768 THEN (1 - (e.embedding_small <=> e_target.embedding_small)) >= :minSimilarity
                WHEN :dimension = 1024 THEN (1 - (e.embedding_medium <=> e_target.embedding_medium)) >= :minSimilarity
                WHEN :dimension = 1536 THEN (1 - (e.embedding_large <=> e_target.embedding_large)) >= :minSimilarity
                ELSE false
              END
        ORDER BY
            CASE
                WHEN :dimension = 768 THEN e.embedding_small <=> e_target.embedding_small
                WHEN :dimension = 1024 THEN e.embedding_medium <=> e_target.embedding_medium
                WHEN :dimension = 1536 THEN e.embedding_large <=> e_target.embedding_large
                ELSE 1.0
            END
        LIMIT :k
        """, nativeQuery = true)
    List<VectorSearchResultProjection> findKNearestNotes(
        @Param("noteId") UUID noteId,
        @Param("dimension") int dimension,
        @Param("userId") UUID userId,
        @Param("minSimilarity") double minSimilarity,
        @Param("k") int k
    );

    // ========================================================================
    // Standard CRUD Operations
    // ========================================================================

    /**
     * Find embedding by note ID (full-note embedding only, not chunks).
     *
     * @param noteId Note ID
     * @return Optional embedding entity
     */
    Optional<NoteEmbeddingEntity> findByNoteIdAndChunkIndexIsNull(UUID noteId);

    /**
     * Find all embeddings for a note (including chunks).
     *
     * @param noteId Note ID
     * @return List of embedding entities
     */
    List<NoteEmbeddingEntity> findByNoteId(UUID noteId);

    /**
     * Find all embeddings for a note with specific model.
     *
     * @param noteId Note ID
     * @param model  Model name
     * @return List of embedding entities
     */
    List<NoteEmbeddingEntity> findByNoteIdAndModel(UUID noteId, String model);

    /**
     * Check if embedding exists for a note.
     *
     * @param noteId Note ID
     * @return true if at least one embedding exists
     */
    boolean existsByNoteId(UUID noteId);

    /**
     * Delete all embeddings for a note (including chunks).
     *
     * @param noteId Note ID
     * @return Number of deleted records
     */
    @Modifying
    @Query("DELETE FROM NoteEmbeddingEntity e WHERE e.noteId = :noteId")
    int deleteByNoteId(@Param("noteId") UUID noteId);

    /**
     * Delete embeddings for specific note and model.
     *
     * @param noteId Note ID
     * @param model  Model name
     * @return Number of deleted records
     */
    @Modifying
    @Query("DELETE FROM NoteEmbeddingEntity e WHERE e.noteId = :noteId AND e.model = :model")
    int deleteByNoteIdAndModel(@Param("noteId") UUID noteId, @Param("model") String model);

    /**
     * Count total embeddings in database.
     *
     * @return Total count
     */
    @Query("SELECT COUNT(e) FROM NoteEmbeddingEntity e")
    long countAllEmbeddings();

    /**
     * Count embeddings for specific user.
     *
     * @param userId User ID
     * @return Count for user
     */
    @Query(value = """
        SELECT COUNT(e.id)
        FROM note_embeddings e
        INNER JOIN notes n ON e.note_id = n.id
        WHERE n.user_id = :userId
        """, nativeQuery = true)
    long countByUserId(@Param("userId") UUID userId);

    /**
     * Get all note IDs that have embeddings.
     *
     * @return List of note IDs
     */
    @Query("SELECT DISTINCT e.noteId FROM NoteEmbeddingEntity e WHERE e.chunkIndex IS NULL")
    List<UUID> findAllIndexedNoteIds();

    /**
     * Get all note IDs for user that have embeddings.
     *
     * @param userId User ID
     * @return List of note IDs
     */
    @Query(value = """
        SELECT DISTINCT e.note_id
        FROM note_embeddings e
        INNER JOIN notes n ON e.note_id = n.id
        WHERE n.user_id = :userId
          AND e.chunk_index IS NULL
        """, nativeQuery = true)
    List<UUID> findIndexedNoteIdsByUserId(@Param("userId") UUID userId);
}
