package org.art.vertex.web.note.search.dto;

import lombok.Builder;
import org.art.vertex.web.note.dto.NoteDto;

@Builder
public record SearchHitDto(
    NoteDto note,
    double score
) {
}
