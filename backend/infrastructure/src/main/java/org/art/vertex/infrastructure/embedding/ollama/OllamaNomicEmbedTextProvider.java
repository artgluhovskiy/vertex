package org.art.vertex.infrastructure.embedding.ollama;

import org.art.vertex.domain.note.search.model.EmbeddingDimension;
import org.art.vertex.domain.note.search.model.EmbeddingModel;

import java.time.Duration;

/**
 * Ollama embedding provider for nomic-embed-text model.
 * <p>
 * Model characteristics:
 * - Dimension: 768 (SMALL)
 * - Context length: 8192 tokens
 * - Performance: Fast (local execution)
 * - Quality: Good for general-purpose semantic search
 * <p>
 * Nomic Embed Text is designed for semantic search and retrieval tasks.
 * It provides a good balance between speed and quality for most use cases.
 */
public class OllamaNomicEmbedTextProvider extends AbstractOllamaEmbeddingProvider {

    public OllamaNomicEmbedTextProvider(String baseUrl, Duration timeout) {
        super(baseUrl, timeout);
    }

    @Override
    public EmbeddingModel getSupportedModel() {
        return EmbeddingModel.OLLAMA_NOMIC_EMBED_TEXT_SMALL;
    }

    @Override
    public String getModelName() {
        return EmbeddingModel.OLLAMA_NOMIC_EMBED_TEXT_SMALL.getModelName();
    }

    @Override
    public int getDimension() {
        return EmbeddingDimension.SMALL.getDim();
    }

    @Override
    public int getMaxTextLength() {
        // Nomic supports ~8k tokens = ~32k characters
        return 32000;
    }
}
