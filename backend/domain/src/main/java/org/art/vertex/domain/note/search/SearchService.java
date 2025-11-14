package org.art.vertex.domain.note.search;

import org.art.vertex.domain.note.search.model.SearchQuery;
import org.art.vertex.domain.note.search.model.SearchResult;

public interface SearchService {

    SearchResult search(SearchQuery query);
}
