package org.art.vertex.infrastructure.embedding.ollama;

import lombok.extern.slf4j.Slf4j;
import org.art.vertex.infrastructure.embedding.EmbeddingGenerationException;
import org.art.vertex.infrastructure.embedding.EmbeddingProvider;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Abstract base class for Ollama embedding providers.
 * Provides common functionality for all Ollama-based embedding models.
 * <p>
 * Ollama API endpoints:
 * - POST /api/embeddings - Generate embeddings
 * - GET /api/tags - List available models
 * <p>
 */
@Slf4j
public abstract class AbstractOllamaEmbeddingProvider implements EmbeddingProvider {

    protected final WebClient webClient;

    protected final Duration timeout;

    protected AbstractOllamaEmbeddingProvider(String baseUrl, Duration timeout) {
        this.webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .build();
        this.timeout = timeout;

        log.info("Initialized Ollama provider: model={}, dimension={}, baseUrl={}, timeout={}",
            getModelName(), getDimension(), baseUrl, timeout);
    }

    @Override
    public List<Float> embed(String text) {
        log.debug("Generating embedding with Ollama: model={}, textLength={}",
            getModelName(), text.length());

        try {
            Map<String, Object> request = Map.of(
                "model", getModelName(),
                "prompt", text,
                "stream", false
            );

            OllamaEmbeddingResponse response = webClient.post()
                .uri("/api/embeddings")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OllamaEmbeddingResponse.class)
                .timeout(timeout)
                .doOnError(error -> log.error("Ollama API error: model={}, error={}",
                    getModelName(), error.getMessage()))
                .block();

            if (response == null || response.embedding() == null) {
                throw new EmbeddingGenerationException(
                    "Received null response from Ollama for model: " + getModelName());
            }

            List<Float> embedding = convertToFloatList(response.embedding());
            normalizeVector(embedding);

            log.trace("Embedding generated successfully: model={}, dimension={}",
                getModelName(), embedding.size());

            return embedding;

        } catch (WebClientResponseException e) {
            log.error("Ollama HTTP error: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new EmbeddingGenerationException(
                "Ollama API HTTP error for model %s: %s".formatted(getModelName(), e.getMessage()), e
            );
        } catch (Exception e) {
            log.error("Failed to generate embedding with Ollama: model={}", getModelName(), e);
            throw new EmbeddingGenerationException(
                "Embedding generation failed for model %s".formatted(getModelName()), e
            );
        }
    }

    @Override
    public List<List<Float>> embedAll(List<String> texts) {
        log.debug("Batch embedding generation: model={}, count={}", getModelName(), texts.size());

        // Ollama doesn't have native batch API, so process sequentially
        // TODO: Consider parallelization with rate limiting
        List<List<Float>> embeddings = new ArrayList<>(texts.size());
        for (int i = 0; i < texts.size(); i++) {
            try {
                embeddings.add(embed(texts.get(i)));
            } catch (Exception e) {
                log.error("Failed to embed text at index {}: {}", i, e.getMessage());
                throw new EmbeddingGenerationException(
                    "Batch embedding failed at index %d".formatted(i), e);
            }
        }

        log.debug("Batch embedding completed: model={}, count={}", getModelName(), texts.size());
        return embeddings;
    }

    @Override
    public boolean isReady() {
        try {
            log.trace("Checking Ollama readiness: model={}", getModelName());

            String response = webClient.get()
                .uri("/api/tags")
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(error -> {
                    log.warn("Ollama health check failed: model={}, error={}",
                        getModelName(), error.getMessage());
                    return Mono.just("{}");
                })
                .block();

            boolean ready = response != null && response.contains("models");

            if (ready) {
                log.trace("Ollama provider is ready: model={}", getModelName());
            } else {
                log.warn("Ollama provider not ready: model={}", getModelName());
            }

            return ready;

        } catch (Exception e) {
            log.warn("Ollama readiness check failed: model={}", getModelName(), e);
            return false;
        }
    }

    @Override
    public int getMaxTextLength() {
        // Most Ollama models support ~8k tokens
        return 8192;
    }

    /**
     * Convert list of Double to list of Float.
     * Ollama returns doubles, but we use floats for efficiency.
     *
     * @param doubles List of doubles from Ollama
     * @return List of floats
     */
    protected List<Float> convertToFloatList(List<Double> doubles) {
        List<Float> floats = new ArrayList<>(doubles.size());
        for (Double d : doubles) {
            floats.add(d.floatValue());
        }
        return floats;
    }

    /**
     * Normalize vector using L2 norm.
     * Ensures cosine similarity works correctly.
     * <p>
     * Formula: v_normalized = v / ||v||
     * where ||v|| = sqrt(sum(v_i^2))
     *
     * @param vector Vector to normalize (modified in-place)
     */
    protected void normalizeVector(List<Float> vector) {
        // Calculate L2 norm
        double sumSquares = 0.0;
        for (Float value : vector) {
            sumSquares += value * value;
        }
        double norm = Math.sqrt(sumSquares);

        // Normalize if norm is not zero
        if (norm > 0.0) {
            for (int i = 0; i < vector.size(); i++) {
                vector.set(i, (float) (vector.get(i) / norm));
            }
        }
    }
}
