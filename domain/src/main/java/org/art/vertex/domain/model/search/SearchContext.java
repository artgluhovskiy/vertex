package org.art.vertex.domain.model.search;

import lombok.Builder;
import lombok.Value;
import org.art.vertex.domain.model.user.User;

import java.util.Map;

@Value
@Builder
public class SearchContext {

    User user;

    boolean includeArchived;

    boolean includeDeleted;

    @Builder.Default
    Map<String, Object> parameters = Map.of();
}