package org.art.vertex.domain.note.search.model;

import lombok.Builder;
import lombok.Value;
import org.art.vertex.domain.note.model.Note;

import java.util.List;
import java.util.Map;

@Value
@Builder
public class SearchHit {

    Note note;

    double score;

    SearchType matchType;

    @Builder.Default
    List<String> highlights = List.of();

    @Builder.Default
    Map<String, Object> metadata = Map.of();
}