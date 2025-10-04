package org.art.vertex.domain.shared.model.search;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
@Builder
public class SearchResult {

    @Builder.Default
    List<SearchHit> hits = List.of();

    long totalHits;

    long searchTimeMs;

    SearchType type;

    @Builder.Default
    Map<String, Object> metadata = Map.of();
}