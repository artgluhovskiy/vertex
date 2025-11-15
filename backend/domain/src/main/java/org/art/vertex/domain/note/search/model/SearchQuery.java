package org.art.vertex.domain.note.search.model;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class SearchQuery {

    String q;

    UUID userId;

    SearchType type;

    @Builder.Default
    int pageSize = 10;

    @Builder.Default
    int pageNumber = 1;

    @Builder.Default
    Double minScore = 0.5;

    @Builder.Default
    EmbeddingModel embeddingModel = EmbeddingModel.OLLAMA_NOMIC_EMBED_TEXT_SMALL;
}