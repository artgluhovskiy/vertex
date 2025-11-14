package org.art.vertex.infrastructure.note.search.persistence;

import org.art.vertex.domain.note.model.Note;
import org.art.vertex.domain.note.search.model.SearchHit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Persistence service interface for vector embedding operations.
 */
public interface NoteEmbeddingPersistenceService {

    void createEmbedding(
        UUID noteId,
        List<Float> vector,
        String model,
        int dimension,
        LocalDateTime createdAt
    );

    void updateEmbedding(
        UUID noteId,
        List<Float> vector,
        String model,
        int dimension,
        LocalDateTime updatedAt
    );

    void deleteEmbedding(UUID noteId);

    List<SearchHit> searchByVector(
        List<Float> queryVector,
        String model,
        UUID userId,
        double minSimilarity,
        int limit
    );

    List<Note> findSimilarNotes(
        UUID noteId,
        double minSimilarity,
        int k
    );
}
