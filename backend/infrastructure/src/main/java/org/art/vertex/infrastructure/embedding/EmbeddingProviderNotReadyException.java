package org.art.vertex.infrastructure.embedding;

import lombok.Getter;
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
@Getter
public class EmbeddingProviderNotReadyException extends RuntimeException {

    private final EmbeddingModel model;

    public EmbeddingProviderNotReadyException(EmbeddingModel model) {
        super(String.format("Embedding provider not ready for model: %s", model));
        this.model = model;
    }

    public EmbeddingProviderNotReadyException(String message, EmbeddingModel model) {
        super(message);
        this.model = model;
    }
}
