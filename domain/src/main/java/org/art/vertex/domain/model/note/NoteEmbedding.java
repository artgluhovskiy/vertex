package org.art.vertex.domain.model.note;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Value
@Builder
public class NoteEmbedding {

    UUID id;

    Note note;

    List<Float> embedding;

    LocalDateTime createdTs;

    LocalDateTime updatedTs;

    public static NoteEmbedding create(
        UUID id,
        Note note,
        List<Float> embedding,
        LocalDateTime ts
    ) {
        return NoteEmbedding.builder()
            .id(id)
            .note(note)
            .embedding(embedding)
            .createdTs(ts)
            .updatedTs(ts)
            .build();
    }
}