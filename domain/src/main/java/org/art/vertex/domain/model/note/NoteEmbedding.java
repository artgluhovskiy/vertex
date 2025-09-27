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

    @Builder.Default
    List<Float> embedding = List.of();

    String model;

    Integer dim;

    LocalDateTime createdTs;

    LocalDateTime updatedTs;

    public static NoteEmbedding create(
        UUID id,
        Note note,
        List<Float> embedding,
        String model,
        Integer dimension,
        LocalDateTime ts
    ) {
        return NoteEmbedding.builder()
            .id(id)
            .note(note)
            .embedding(embedding)
            .model(model)
            .dim(dimension)
            .createdTs(ts)
            .updatedTs(ts)
            .build();
    }
}