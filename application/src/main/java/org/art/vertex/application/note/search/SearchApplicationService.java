package org.art.vertex.application.note.search;

import org.art.vertex.application.note.search.SearchCommand;
import org.art.vertex.application.note.search.SearchResultDto;

public interface SearchApplicationService {

    SearchResultDto search(SearchCommand command);

    SearchResultDto searchSemantic(SearchCommand command);

    SearchResultDto searchFullText(SearchCommand command);

    SearchResultDto searchHybrid(SearchCommand command);
}