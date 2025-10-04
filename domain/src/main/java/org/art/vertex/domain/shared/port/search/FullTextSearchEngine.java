package org.art.vertex.domain.shared.port.search;

import org.art.vertex.domain.note.Note;
import org.art.vertex.domain.shared.model.search.SearchQuery;
import org.art.vertex.domain.shared.model.search.SearchResult;

import java.util.UUID;

public interface FullTextSearchEngine {

    void index(Note note);

    void remove(UUID noteId);

    void reindex(Note note);

    SearchResult search(SearchQuery query);

    void optimize();
}