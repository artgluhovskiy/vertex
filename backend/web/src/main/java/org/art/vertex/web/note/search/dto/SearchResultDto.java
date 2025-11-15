package org.art.vertex.web.note.search.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record SearchResultDto(
    List<SearchHitDto> hits,
    long totalHits
) {
}
