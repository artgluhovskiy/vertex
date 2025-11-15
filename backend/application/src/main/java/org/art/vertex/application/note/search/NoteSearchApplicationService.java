package org.art.vertex.application.note.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.art.vertex.application.note.search.command.SearchCommand;
import org.art.vertex.domain.note.search.VectorSearchService;
import org.art.vertex.domain.note.search.model.EmbeddingModel;
import org.art.vertex.domain.note.search.model.SearchQuery;
import org.art.vertex.domain.note.search.model.SearchResult;
import org.art.vertex.domain.note.search.model.SearchType;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
public class NoteSearchApplicationService {

    private final VectorSearchService vectorSearchService;
    // Future: inject FullTextSearchService and HybridSearchService when implemented

    @Transactional(readOnly = true)
    public SearchResult search(SearchCommand command) {
        log.debug("Search requested - query: '{}', type: {}, userId: {}",
            command.getQuery(), command.getType(), command.getUserId());

        // For now, only semantic search is implemented
        // Full-text and hybrid search will be added in future iterations
        if (command.getType() == null || command.getType() == SearchType.SEMANTIC) {
            return searchSemantic(command);
        }

        // Fallback to semantic for any other type
        log.warn("Search type {} not yet implemented, using SEMANTIC", command.getType());
        return searchSemantic(command);
    }

    @Transactional(readOnly = true)
    public SearchResult searchSemantic(SearchCommand command) {
        log.debug("Semantic search - query: '{}', userId: {}",
            command.getQuery(), command.getUserId());

        SearchQuery searchQuery = SearchQuery.builder()
            .q(command.getQuery())
            .userId(command.getUserId())
            .type(SearchType.SEMANTIC)
            .pageSize(command.getMaxResults() != null ? command.getMaxResults() : 20)
            .pageNumber(1)
            .embeddingModel(EmbeddingModel.OLLAMA_NOMIC_EMBED_TEXT_SMALL)
            .build();

        SearchResult result = vectorSearchService.search(searchQuery);

        log.info("Semantic search completed - query: '{}', hits: {}",
            command.getQuery(), result.getTotalHits());

        return result;
    }

    @Transactional(readOnly = true)
    public SearchResult searchFullText(SearchCommand command) {
        log.warn("Full-text search not yet implemented, falling back to semantic search");
        // TODO: Implement when FullTextSearchService is ready
        return searchSemantic(command);
    }

    @Transactional(readOnly = true)
    public SearchResult searchHybrid(SearchCommand command) {
        log.warn("Hybrid search not yet implemented, falling back to semantic search");
        // TODO: Implement when HybridSearchService is ready
        return searchSemantic(command);
    }
}
