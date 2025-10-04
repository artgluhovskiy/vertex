package org.art.vertex.application.service;

import org.art.vertex.application.command.SearchCommand;
import org.art.vertex.application.dto.SearchResultDto;

public interface SearchApplicationService {

    SearchResultDto search(SearchCommand command);

    SearchResultDto searchSemantic(SearchCommand command);

    SearchResultDto searchFullText(SearchCommand command);

    SearchResultDto searchHybrid(SearchCommand command);
}