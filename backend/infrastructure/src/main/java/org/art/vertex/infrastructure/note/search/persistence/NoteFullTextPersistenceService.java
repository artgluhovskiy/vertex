package org.art.vertex.infrastructure.note.search.persistence;

import org.art.vertex.domain.note.search.model.SearchHit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Persistence service interface for full-text search operations.
 */
public interface NoteFullTextPersistenceService {

    void createFullTextIndex(UUID noteId, String indexText, LocalDateTime createdAt);

    void updateFullTextIndex(UUID noteId, String indexText, LocalDateTime updatedAt);

    void deleteFullTextIndex(UUID noteId);

    List<SearchHit> searchFullText(
        String query,
        UUID userId,
        double minRank,
        int limit
    );
}
