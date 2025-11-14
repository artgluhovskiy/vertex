package org.art.vertex.domain.note.search;

import org.art.vertex.domain.note.search.model.SearchQuery;
import org.art.vertex.domain.note.search.model.SearchResult;

public interface SearchOperations {

    SearchResult search(SearchQuery query);

    SearchResult searchFullText(SearchQuery query);

    SearchResult searchSemantic(SearchQuery query);

    SearchResult searchHybrid(SearchQuery query);
}
