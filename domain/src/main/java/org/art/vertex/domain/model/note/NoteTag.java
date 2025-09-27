package org.art.vertex.domain.model.note;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class NoteTag {

    Note note;

    String tag;

    LocalDateTime createdTs;

    LocalDateTime updatedTs;

    public static NoteTag create(
        Note note,
        String tag,
        LocalDateTime ts
    ) {
        return NoteTag.builder()
            .note(note)
            .tag(tag)
            .createdTs(ts)
            .updatedTs(ts)
            .build();
    }
}