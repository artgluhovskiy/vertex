package org.art.vertex.application.note.search.command;

import lombok.Builder;
import lombok.Value;
import org.art.vertex.domain.note.search.model.SearchType;

import java.util.UUID;

@Value
@Builder
public class SearchCommand {
    String query;
    UUID userId;
    SearchType type;
    UUID dirId;
    Integer maxResults;
}