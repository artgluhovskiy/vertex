package org.art.vertex.domain.note.search.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class SearchResult {

    @Builder.Default
    List<SearchHit> hits = List.of();

    long totalHits;
}