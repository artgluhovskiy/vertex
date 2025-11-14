package org.art.vertex.domain.note.search.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmbeddingModel {

    OLLAMA_NOMIC_EMBED_TEXT_SMALL(ModelProvider.OLLAMA, "nomic-embed-text", EmbeddingDimension.SMALL),

    OLLAMA_MXBAI_EMBED_LARGE(ModelProvider.OLLAMA, "mxbai-embed-large", EmbeddingDimension.MEDIUM),

    OPENAI_TEXT_EMBEDDING_3_SMALL_LARGE(ModelProvider.OPENAI, "text-embedding-3-small", EmbeddingDimension.LARGE);

    private final ModelProvider provider;
    private final String modelName;
    private final EmbeddingDimension dimension;
}
