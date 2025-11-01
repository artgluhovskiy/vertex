package org.art.vertex.domain.note.search;

import org.art.vertex.domain.note.model.Note;
import org.art.vertex.domain.note.search.model.SearchQuery;
import org.art.vertex.domain.note.search.model.SearchResult;

import java.util.UUID;

public interface FullTextSearchEngine {

    void index(Note note);

    void remove(UUID noteId);

    void reindex(Note note);

    SearchResult search(SearchQuery query);

    void optimize();
}