package org.art.vertex.infrastructure.note.search.mapper;

import org.art.vertex.domain.note.model.NoteEmbedding;
import org.art.vertex.domain.note.search.model.EmbeddingDimension;
import org.art.vertex.infrastructure.note.search.entity.NoteEmbeddingEntity;

import java.util.ArrayList;
import java.util.List;

public class NoteEmbeddingEntityMapper {

    public NoteEmbeddingEntity toEntity(NoteEmbedding domain) {
        NoteEmbeddingEntity entity = NoteEmbeddingEntity.builder()
            .noteId(domain.getNoteId())
            .model(domain.getModel())
            .dimension(domain.getDimensionValue())
            .chunkIndex(domain.getChunkIndex())
            .chunkText(domain.getChunkText())
            .createdAt(domain.getCreatedTs())
            .updatedAt(domain.getUpdatedTs())
            .build();

        entity.setId(domain.getId());
        entity.setEmbeddingForDimension(domain.getDimension(), domain.getEmbedding());

        return entity;
    }

    public NoteEmbedding toDomain(NoteEmbeddingEntity entity) {
        EmbeddingDimension dimension = EmbeddingDimension.fromValue(entity.getDimension());
        float[] vectorArray = entity.getEmbeddingForDimension(dimension);
        List<Float> vector = vectorArray != null ? floatArrayToList(vectorArray) : List.of();

        return NoteEmbedding.builder()
            .id(entity.getId())
            .noteId(entity.getNoteId())
            .embedding(vector)
            .model(entity.getModel())
            .dimension(dimension)
            .chunkIndex(entity.getChunkIndex())
            .chunkText(entity.getChunkText())
            .createdTs(entity.getCreatedAt())
            .updatedTs(entity.getUpdatedAt())
            .build();
    }

    private List<Float> floatArrayToList(float[] array) {
        List<Float> list = new ArrayList<>(array.length);
        for (float value : array) {
            list.add(value);
        }
        return list;
    }
}
