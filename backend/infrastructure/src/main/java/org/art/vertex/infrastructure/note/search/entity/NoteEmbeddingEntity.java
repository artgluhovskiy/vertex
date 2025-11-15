package org.art.vertex.infrastructure.note.search.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.art.vertex.domain.note.search.model.EmbeddingDimension;
import org.art.vertex.infrastructure.shared.BaseEntity;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "note_embeddings")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NoteEmbeddingEntity extends BaseEntity {

    @Column(name = "note_id", nullable = false)
    private UUID noteId;

    @Column(name = "embedding_small", columnDefinition = "vector(768)")
    private float[] embeddingSmall;

    @Column(name = "embedding_medium", columnDefinition = "vector(1024)")
    private float[] embeddingMedium;

    @Column(name = "embedding_large", columnDefinition = "vector(1536)")
    private float[] embeddingLarge;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private Integer dimension;

    @Column(name = "chunk_index")
    private Integer chunkIndex;

    @Column(name = "chunk_text", columnDefinition = "TEXT")
    private String chunkText;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public float[] getEmbeddingForDimension(EmbeddingDimension dimension) {
        return switch (dimension) {
            case SMALL -> embeddingSmall;
            case MEDIUM -> embeddingMedium;
            case LARGE -> embeddingLarge;
        };
    }

    public void setEmbeddingForDimension(EmbeddingDimension dimension, float[] embedding) {
        switch (dimension) {
            case SMALL -> this.embeddingSmall = embedding;
            case MEDIUM -> this.embeddingMedium = embedding;
            case LARGE -> this.embeddingLarge = embedding;
        }
    }

    public void setEmbeddingForDimension(EmbeddingDimension dimension, List<Float> embeddingList) {
        float[] embedding = new float[embeddingList.size()];
        for (int i = 0; i < embeddingList.size(); i++) {
            embedding[i] = embeddingList.get(i);
        }
        setEmbeddingForDimension(dimension, embedding);
    }

    public boolean hasEmbeddingForDimension(EmbeddingDimension dimension) {
        float[] embedding = getEmbeddingForDimension(dimension);
        return embedding != null && embedding.length > 0;
    }

    public boolean isFullNoteEmbedding() {
        return chunkIndex == null;
    }

    public boolean isChunkedEmbedding() {
        return chunkIndex != null;
    }
}
