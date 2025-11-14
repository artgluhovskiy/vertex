package org.art.vertex.domain.note.search.model;

import lombok.Builder;
import lombok.Value;
import org.art.vertex.domain.note.model.Note;

@Value
@Builder
public class SearchHit {

    Note note;

    double score;
}