package org.art.vertex.web.note.search.mapper;

import org.art.vertex.application.note.search.command.SearchCommand;
import org.art.vertex.domain.note.search.model.SearchType;
import org.art.vertex.web.note.search.request.SearchRequest;

import java.util.UUID;

public class SearchCommandMapper {

    public SearchCommand toCommand(SearchRequest request, UUID userId) {
        return SearchCommand.builder()
            .query(request.query())
            .userId(userId)
            .type(request.type() != null ? request.type() : SearchType.SEMANTIC)
            .maxResults(request.maxResults())
            .build();
    }
}
