package org.art.vertex.infrastructure.note.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.art.vertex.domain.note.NoteEmbeddingRepository;
import org.art.vertex.domain.note.model.Note;
import org.art.vertex.domain.note.model.NoteEmbedding;
import org.art.vertex.domain.note.search.model.SearchHit;
import org.art.vertex.infrastructure.note.search.entity.NoteEmbeddingEntity;
import org.art.vertex.infrastructure.note.search.jpa.NoteEmbeddingJpaRepository;
import org.art.vertex.infrastructure.note.search.jpa.projection.VectorSearchResultProjection;
import org.art.vertex.infrastructure.note.search.mapper.NoteEmbeddingEntityMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class DefaultNoteEmbeddingRepository implements NoteEmbeddingRepository {

    private final NoteEmbeddingJpaRepository jpaRepository;
    private final NoteEmbeddingEntityMapper mapper;

    @Override
    @Transactional
    public NoteEmbedding save(NoteEmbedding embedding) {
        log.debug("Saving embedding for note: {}", embedding.getNoteId());

        NoteEmbeddingEntity entity = mapper.toEntity(embedding);
        NoteEmbeddingEntity savedEntity = jpaRepository.save(entity);

        log.trace("Embedding saved with ID: {}", savedEntity.getId());
        return mapper.toDomain(savedEntity);
    }

    @Override
    @Transactional
    public NoteEmbedding update(NoteEmbedding embedding) {
        log.debug("Updating embedding for note: {}", embedding.getNoteId());

        NoteEmbeddingEntity existingEntity = jpaRepository.findById(embedding.getId())
            .orElseThrow(() -> new IllegalArgumentException(
                "Embedding not found for update: " + embedding.getId()));

        NoteEmbeddingEntity updatedEntity = mapper.toEntity(embedding);
        updatedEntity.setId(existingEntity.getId());

        NoteEmbeddingEntity savedEntity = jpaRepository.save(updatedEntity);

        log.trace("Embedding updated: {}", savedEntity.getId());
        return mapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NoteEmbedding> findByNoteId(UUID noteId) {
        log.debug("Finding embedding for note: {}", noteId);

        return jpaRepository.findByNoteIdAndChunkIndexIsNull(noteId)
            .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public NoteEmbedding getByNoteId(UUID noteId) {
        return findByNoteId(noteId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Embedding not found for note: " + noteId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoteEmbedding> findAllByNoteId(UUID noteId) {
        log.debug("Finding all embeddings for note: {}", noteId);

        return jpaRepository.findByNoteId(noteId).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNoteId(UUID noteId) {
        return jpaRepository.existsByNoteId(noteId);
    }

    @Override
    @Transactional
    public void deleteByNoteId(UUID noteId) {
        log.debug("Deleting embeddings for note: {}", noteId);

        int deletedCount = jpaRepository.deleteByNoteId(noteId);

        log.trace("Deleted {} embedding(s) for note: {}", deletedCount, noteId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SearchHit> searchByVector(
        List<Float> queryVector,
        String model,
        UUID userId,
        double minSimilarity,
        int limit
    ) {
        log.debug("Vector search: user={}, model={}, minSimilarity={}, limit={}",
            userId, model, minSimilarity, limit);

        int dimension = queryVector.size();
        String vectorString = convertToPgVectorString(queryVector);

        List<VectorSearchResultProjection> results = jpaRepository.searchByVector(
            vectorString,
            dimension,
            model,
            userId,
            minSimilarity,
            limit
        );

        log.debug("Found {} results for vector search", results.size());

        return results.stream()
            .map(this::mapProjectionToSearchHit)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UUID> findKNearestNoteIds(
        UUID noteId,
        UUID userId,
        double minSimilarity,
        int k
    ) {
        log.debug("Finding {} nearest notes for note: {}", k, noteId);

        var embeddingOpt = jpaRepository.findByNoteIdAndChunkIndexIsNull(noteId);
        if (embeddingOpt.isEmpty()) {
            log.warn("No embedding found for note: {}", noteId);
            return List.of();
        }

        NoteEmbeddingEntity embedding = embeddingOpt.get();
        int dimension = embedding.getDimension();

        List<VectorSearchResultProjection> results = jpaRepository.findKNearestNotes(
            noteId,
            dimension,
            userId,
            minSimilarity,
            k
        );

        log.debug("Found {} similar notes", results.size());

        return results.stream()
            .map(VectorSearchResultProjection::getNoteId)
            .collect(Collectors.toList());
    }

    /**
     * Convert List<Float> to pgvector string format: "[0.1,0.2,0.3,...]"
     */
    private String convertToPgVectorString(List<Float> vector) {
        return vector.stream()
            .map(String::valueOf)
            .collect(Collectors.joining(",", "[", "]"));
    }

    private SearchHit mapProjectionToSearchHit(VectorSearchResultProjection projection) {
        Note note = Note.builder()
            .id(projection.getNoteId())
            .userId(projection.getUserId())
            .title(projection.getTitle())
            .content(projection.getContent())
            .summary(projection.getSummary())
            .build();

        return SearchHit.builder()
            .note(note)
            .score(projection.getSimilarity())
            .build();
    }
}
