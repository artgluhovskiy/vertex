package org.art.vertex.application.note.search;

import org.art.vertex.application.note.search.command.SearchCommand;
import org.art.vertex.domain.note.search.model.SearchResult;

public interface SearchApplicationService {

    SearchResult search(SearchCommand command);

    SearchResult searchSemantic(SearchCommand command);

    SearchResult searchFullText(SearchCommand command);

    SearchResult searchHybrid(SearchCommand command);
}