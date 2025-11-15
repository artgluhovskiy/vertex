package org.art.vertex.infrastructure.note.search.embedding.ollama;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Response DTO for Ollama embedding API.
 * Maps to the JSON structure returned by Ollama's /api/embeddings endpoint.
 * <p>
 * Example response:
 * <pre>
 * {
 *   "model": "nomic-embed-text",
 *   "embedding": [0.123, -0.456, 0.789, ...]
 * }
 * </pre>
 *
 * @param model     Model name used for embedding
 * @param embedding Embedding vector as list of doubles
 */
public record OllamaEmbeddingResponse(
    @JsonProperty("model") String model,
    @JsonProperty("embedding") List<Double> embedding
) {
}
