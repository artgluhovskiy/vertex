package org.art.vertex.domain.note.search.model;

import lombok.Builder;
import lombok.Value;
import org.art.vertex.domain.directory.model.Directory;
import org.art.vertex.domain.tag.model.Tag;
import org.art.vertex.domain.user.model.User;

import java.util.List;
import java.util.Map;

@Value
@Builder
public class SearchQuery {

    String rawQuery;

    User user;

    SearchType type;

    Directory scope;

    @Builder.Default
    List<Tag> tagFilters = List.of();

    Integer maxResults;

    @Builder.Default
    Double minScore = 0.0;

    @Builder.Default
    boolean includeHighlights = false;

    @Builder.Default
    String embeddingModel = "default";

    @Builder.Default
    Map<String, Object> options = Map.of();
}