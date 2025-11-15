package org.art.vertex.infrastructure.note.search.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.art.vertex.domain.note.NoteEmbeddingRepository;
import org.art.vertex.domain.note.model.Note;
import org.art.vertex.domain.note.model.NoteEmbedding;
import org.art.vertex.domain.note.search.VectorSearchService;
import org.art.vertex.domain.note.search.model.EmbeddingDimension;
import org.art.vertex.domain.note.search.model.EmbeddingModel;
import org.art.vertex.domain.note.search.model.SearchConfiguration;
import org.art.vertex.domain.note.search.model.SearchHit;
import org.art.vertex.domain.note.search.model.SearchQuery;
import org.art.vertex.domain.note.search.model.SearchResult;
import org.art.vertex.domain.shared.time.Clock;
import org.art.vertex.domain.shared.uuid.UuidGenerator;
import org.art.vertex.infrastructure.embedding.EmbeddingProvider;
import org.art.vertex.infrastructure.embedding.EmbeddingProviderFactory;
import org.art.vertex.infrastructure.note.search.indexing.IndexableText;
import org.art.vertex.infrastructure.note.search.indexing.TextIndexingStrategy;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class DefaultVectorSearchService implements VectorSearchService {

    private final NoteEmbeddingRepository embeddingRepository;
    private final EmbeddingProviderFactory providerFactory;
    private final TextIndexingStrategy indexingStrategy;
    private final SearchConfiguration config;
    private final UuidGenerator uuidGenerator;
    private final Clock clock;

    private final EmbeddingModel defaultModel;

    @Override
    @Transactional
    public void index(Note note) {
        log.debug("Indexing note for vector search: {}", note.getId());

        try {
            IndexableText indexableText = indexingStrategy.prepareText(note);
            String text = indexableText.getText();

            log.trace("Prepared index text ({} chars) for note: {}",
                text.length(), note.getId());

            EmbeddingProvider provider = providerFactory.getProvider(defaultModel);

            if (!provider.isReady()) {
                log.warn("Embedding provider {} is not ready, skipping indexing for note: {}",
                    defaultModel, note.getId());
                return;
            }

            List<Float> vector = provider.embed(text);
            EmbeddingDimension dimension = EmbeddingDimension.fromValue(provider.getDimension());

            log.trace("Generated embedding ({}-dimensional) for note: {}",
                provider.getDimension(), note.getId());

            LocalDateTime now = clock.now();
            var existingOpt = embeddingRepository.findByNoteId(note.getId());

            if (existingOpt.isPresent()) {
                NoteEmbedding existing = existingOpt.get();
                NoteEmbedding updated = existing.update(vector, now);
                embeddingRepository.update(updated);
                log.debug("Updated embedding for note: {}", note.getId());
            } else {
                NoteEmbedding newEmbedding = NoteEmbedding.create(
                    uuidGenerator.generate(),
                    note.getId(),
                    vector,
                    provider.getModelName(),
                    dimension,
                    now
                );
                embeddingRepository.save(newEmbedding);
                log.debug("Created new embedding for note: {}", note.getId());
            }

            log.info("Successfully indexed note: {} with model: {}",
                note.getId(), provider.getModelName());

        } catch (Exception e) {
            log.error("Failed to index note: {}", note.getId(), e);
            throw new RuntimeException("Failed to index note: " + note.getId(), e);
        }
    }

    @Override
    @Transactional
    public void remove(UUID noteId) {
        log.debug("Removing embedding for note: {}", noteId);

        embeddingRepository.deleteByNoteId(noteId);

        log.info("Successfully removed embedding for note: {}", noteId);
    }

    @Override
    @Transactional
    public void reindex(Note note) {
        log.debug("Reindexing note: {}", note.getId());

        // Remove old embedding and create new one
        remove(note.getId());
        index(note);

        log.info("Successfully reindexed note: {}", note.getId());
    }

    @Override
    public void optimizeIndex() {
        log.info("Vector index optimization not implemented for pgvector");
        // pgvector indexes are automatically optimized by PostgreSQL
        // Could implement VACUUM ANALYZE on note_embeddings table if needed
    }

    @Override
    @Transactional(readOnly = true)
    public SearchResult search(SearchQuery query) {
        log.debug("Executing vector search, query: {}", query.getQ());

        try {
            EmbeddingModel model = Optional.ofNullable(query.getEmbeddingModel()).orElse(defaultModel);

            EmbeddingProvider provider = providerFactory.getProvider(model);

            if (!provider.isReady()) {
                log.warn("Embedding provider {} is not ready", model);
                return SearchResult.builder()
                    .hits(List.of())
                    .totalHits(0)
                    .build();
            }

            // Generate query embedding
            List<Float> queryVector = provider.embed(query.getQ());

            log.trace("Generated query embedding ({}-dimensional)",
                queryVector.size());

            double minSimilarity = Optional.ofNullable(query.getMinScore())
                .orElse(config.getVectorMinSimilarityThreshold());

            int limit = query.getPageSize();

            List<SearchHit> hits = embeddingRepository.searchByVector(
                queryVector,
                provider.getModelName(),
                query.getUserId(),
                minSimilarity,
                limit
            );

            log.debug("Found {} results for vector search", hits.size());

            return SearchResult.builder()
                .hits(hits)
                .totalHits(hits.size())
                .build();

        } catch (Exception e) {
            log.error("Vector search failed for query: {}", query.getQ(), e);
            return SearchResult.builder()
                .hits(List.of())
                .totalHits(0)
                .build();
        }
    }
}
