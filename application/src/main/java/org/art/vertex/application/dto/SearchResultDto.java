package org.art.vertex.application.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
@Builder
public class SearchResultDto {
    List<SearchHitDto> hits;
    long totalHits;
    long searchTimeMs;
    String searchType;
    Map<String, Object> metadata;
}