package org.art.vertex.application.note.search;

import lombok.Builder;
import lombok.Value;
import org.art.vertex.domain.note.search.model.SearchType;

import java.util.List;
import java.util.UUID;

@Value
@Builder
public class SearchCommand {
    String query;
    UUID userId;
    SearchType type;
    UUID directoryId;
    List<UUID> tagIds;
    Integer maxResults;
    boolean includeGraph;
}