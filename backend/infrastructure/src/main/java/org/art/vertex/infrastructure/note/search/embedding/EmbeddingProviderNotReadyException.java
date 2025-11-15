package org.art.vertex.infrastructure.note.search.embedding;

import org.art.vertex.domain.note.search.model.EmbeddingModel;

/**
 * Exception thrown when embedding provider is not ready to serve requests.
 * This is thrown by the service layer when health check fails.
 * <p>
 * Indicates that:
 * - Provider endpoint is unreachable
 * - Provider is starting up
 * - Model is not loaded yet
 * - Authentication failed
 */
public class EmbeddingProviderNotReadyException extends RuntimeException {

    private final EmbeddingModel model;

    /**
     * Create exception with model.
     *
     * @param model Embedding model that is not ready
     */
    public EmbeddingProviderNotReadyException(EmbeddingModel model) {
        super(String.format("Embedding provider not ready for model: %s", model));
        this.model = model;
    }

    /**
     * Create exception with message and model.
     *
     * @param message Error message
     * @param model   Embedding model that is not ready
     */
    public EmbeddingProviderNotReadyException(String message, EmbeddingModel model) {
        super(message);
        this.model = model;
    }

    /**
     * Get the embedding model that is not ready.
     *
     * @return Embedding model
     */
    public EmbeddingModel getModel() {
        return model;
    }
}
