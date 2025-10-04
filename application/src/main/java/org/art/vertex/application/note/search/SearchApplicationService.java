package org.art.vertex.application.note.search;

import org.art.vertex.application.note.search.command.SearchCommand;
import org.art.vertex.application.note.search.dto.SearchResultDto;

public interface SearchApplicationService {

    SearchResultDto search(SearchCommand command);

    SearchResultDto searchSemantic(SearchCommand command);

    SearchResultDto searchFullText(SearchCommand command);

    SearchResultDto searchHybrid(SearchCommand command);
}