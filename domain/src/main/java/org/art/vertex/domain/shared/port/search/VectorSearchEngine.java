package org.art.vertex.domain.shared.port.search;

import org.art.vertex.domain.shared.model.embedding.Embedding;
import org.art.vertex.domain.note.Note;
import org.art.vertex.domain.shared.model.search.SearchQuery;
import org.art.vertex.domain.shared.model.search.SearchResult;

import java.util.List;
import java.util.UUID;

public interface VectorSearchEngine {

    void index(UUID noteId, Embedding embedding);

    void remove(UUID noteId);

    SearchResult search(Embedding queryEmbedding, SearchQuery query);

    List<Note> findSimilar(UUID noteId, int k);
}