package org.art.vertex.web.note.search.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
@Builder
public class SearchHitDto {
    UUID noteId;
    String title;
    String content;
    double score;
    String matchType;
    List<String> highlights;
}
