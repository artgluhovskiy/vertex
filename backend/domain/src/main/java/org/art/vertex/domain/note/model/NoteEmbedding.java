package org.art.vertex.domain.note.model;

import lombok.Builder;
import lombok.Value;
import org.art.vertex.domain.note.search.model.EmbeddingDimension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class NoteEmbedding {

    UUID id;

    UUID noteId;

    @Builder.Default
    List<Float> embedding = List.of();

    String model;

    EmbeddingDimension dimension;

    Integer chunkIndex;

    String chunkText;

    LocalDateTime createdTs;

    LocalDateTime updatedTs;

    public boolean isFullNoteEmbedding() {
        return chunkIndex == null;
    }

    public boolean isChunkedEmbedding() {
        return chunkIndex != null;
    }

    public int getDimensionValue() {
        return dimension.getDim();
    }

    public static NoteEmbedding create(
        UUID id,
        UUID noteId,
        List<Float> embedding,
        String model,
        EmbeddingDimension dimension,
        LocalDateTime ts
    ) {
        return NoteEmbedding.builder()
            .id(id)
            .noteId(noteId)
            .embedding(embedding)
            .model(model)
            .dimension(dimension)
            .chunkIndex(null)
            .chunkText(null)
            .createdTs(ts)
            .updatedTs(ts)
            .build();
    }

    public static NoteEmbedding createChunk(
        UUID id,
        UUID noteId,
        List<Float> embedding,
        String model,
        EmbeddingDimension dimension,
        int chunkIndex,
        String chunkText,
        LocalDateTime ts
    ) {
        return NoteEmbedding.builder()
            .id(id)
            .noteId(noteId)
            .embedding(embedding)
            .model(model)
            .dimension(dimension)
            .chunkIndex(chunkIndex)
            .chunkText(chunkText)
            .createdTs(ts)
            .updatedTs(ts)
            .build();
    }

    public NoteEmbedding update(List<Float> newEmbedding, LocalDateTime updatedAt) {
        return toBuilder()
            .embedding(newEmbedding)
            .updatedTs(updatedAt)
            .build();
    }
}
