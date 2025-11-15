package org.art.vertex.infrastructure.embedding;

import org.art.vertex.domain.note.search.model.EmbeddingModel;

import java.util.List;

/**
 * Provider interface for generating text embeddings.
 * Implementations support different embedding models and APIs (Ollama, OpenAI, etc.).
 * <p>
 * Embeddings are dense vector representations of text that capture semantic meaning,
 * enabling similarity-based search and retrieval.
 * <p>
 * All implementations must:
 * - Return normalized vectors (L2 norm = 1.0) for consistent cosine similarity
 * - Handle errors gracefully with {@link EmbeddingGenerationException}
 * - Support health checks via {@link #isReady()}
 * - Be thread-safe for concurrent requests
 *
 * @see EmbeddingProviderFactory for obtaining provider instances
 */
public interface EmbeddingProvider {

    /**
     * Generate embedding for single text.
     * Returns a normalized vector suitable for cosine similarity search.
     *
     * @param text Input text to embed (max length depends on model)
     * @return Embedding vector as list of floats (normalized)
     * @throws EmbeddingGenerationException if embedding generation fails
     * @throws IllegalArgumentException     if text is null or exceeds model limits
     */
    List<Float> embed(String text);

    /**
     * Generate embeddings for multiple texts in batch.
     * More efficient than calling {@link #embed(String)} repeatedly.
     * <p>
     * Implementations may:
     * - Process texts in parallel
     * - Use batch API endpoints
     * - Apply rate limiting
     *
     * @param texts List of texts to embed
     * @return List of embedding vectors (same order as input)
     * @throws EmbeddingGenerationException if any embedding generation fails
     * @throws IllegalArgumentException     if texts is null or empty
     */
    List<List<Float>> embedAll(List<String> texts);

    /**
     * Get supported embedding model enum.
     * Used by {@link EmbeddingProviderFactory} to register providers.
     *
     * @return EmbeddingModel enum value
     */
    EmbeddingModel getSupportedModel();

    /**
     * Get model name/identifier as used by the provider API.
     * Examples: "nomic-embed-text", "mxbai-embed-large", "text-embedding-3-small"
     *
     * @return Model name string
     */
    String getModelName();

    /**
     * Get embedding vector dimension for this provider.
     * Must match one of the supported dimensions: 768 (SMALL), 1024 (MEDIUM), 1536 (LARGE)
     *
     * @return Dimension size
     */
    int getDimension();

    /**
     * Check if provider is ready to generate embeddings.
     * Should verify:
     * - API endpoint is accessible
     * - Authentication is valid
     * - Model is available
     * <p>
     * Called before using provider to fail fast if service is unavailable.
     *
     * @return true if provider can generate embeddings, false otherwise
     */
    boolean isReady();

    int getMaxTextLength();
}
