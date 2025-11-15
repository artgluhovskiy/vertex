package org.art.vertex.infrastructure.embedding;

/**
 * Exception thrown when embedding generation fails.
 * This is a runtime exception to simplify error handling in the service layer.
 * <p>
 * Common causes:
 * - API endpoint unavailable (network error, service down)
 * - Authentication failure (invalid API key)
 * - Rate limiting (too many requests)
 * - Model not found or not loaded
 * - Invalid input (text too long, unsupported format)
 * - Internal provider error
 */
public class EmbeddingGenerationException extends RuntimeException {

    /**
     * Create exception with message.
     *
     * @param message Error message
     */
    public EmbeddingGenerationException(String message) {
        super(message);
    }

    /**
     * Create exception with message and cause.
     *
     * @param message Error message
     * @param cause   Underlying cause
     */
    public EmbeddingGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Create exception with cause only.
     * Message will be taken from cause.
     *
     * @param cause Underlying cause
     */
    public EmbeddingGenerationException(Throwable cause) {
        super(cause);
    }
}
