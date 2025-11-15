package org.art.vertex.web.note.search.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import org.art.vertex.domain.note.search.model.SearchType;

@Builder
public record SearchRequest(

    @NotBlank(message = "Search query is required")
    String query,

    SearchType type,

    @Positive(message = "Max results must be positive")
    Integer maxResults
) {
}
