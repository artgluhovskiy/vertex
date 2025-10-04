package org.art.vertex.domain.note.search;

import org.art.vertex.domain.note.search.model.SearchQuery;
import org.art.vertex.domain.note.search.model.SearchResult;
import org.art.vertex.domain.shared.embedding.Embedding;
import org.art.vertex.domain.note.model.Note;

import java.util.List;
import java.util.UUID;

public interface VectorSearchEngine {

    void index(UUID noteId, Embedding embedding);

    void remove(UUID noteId);

    SearchResult search(Embedding queryEmbedding, SearchQuery query);

    List<Note> findSimilar(UUID noteId, int k);
}