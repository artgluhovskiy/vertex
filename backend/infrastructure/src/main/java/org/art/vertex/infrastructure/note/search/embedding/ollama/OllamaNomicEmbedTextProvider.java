package org.art.vertex.infrastructure.note.search.embedding.ollama;

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
 * <p>
 * Requirements:
 * - Ollama running locally or accessible via network
 * - Model pulled: `ollama pull nomic-embed-text`
 * <p>
 * Configuration:
 * - Registered conditionally in NoteInfrastructureConfig
 * - Controlled by: embedding.providers.ollama.enabled=true (default: true)
 * - embedding.providers.ollama.base-url=http://localhost:11434
 * - embedding.providers.ollama.timeout-seconds=30
 */
public class OllamaNomicEmbedTextProvider extends AbstractOllamaEmbeddingProvider {

    private static final String MODEL_NAME = "nomic-embed-text";
    private static final int DIMENSION = 768;

    /**
     * Constructor with explicit configuration parameters.
     *
     * @param baseUrl Ollama base URL
     * @param timeout Request timeout duration
     */
    public OllamaNomicEmbedTextProvider(String baseUrl, Duration timeout) {
        super(baseUrl, timeout);
    }

    @Override
    public EmbeddingModel getSupportedModel() {
        return EmbeddingModel.OLLAMA_NOMIC_EMBED_TEXT_SMALL;
    }

    @Override
    public String getModelName() {
        return MODEL_NAME;
    }

    @Override
    public int getDimension() {
        return DIMENSION;
    }

    @Override
    public int getMaxTextLength() {
        // Nomic supports ~8k tokens = ~32k characters
        return 32000;
    }

    @Override
    public long getEstimatedGenerationTimeMs() {
        // Nomic is very fast locally
        return 300L; // 300ms average
    }
}
