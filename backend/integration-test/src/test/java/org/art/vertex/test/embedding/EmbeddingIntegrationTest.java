package org.art.vertex.test.embedding;

import lombok.extern.slf4j.Slf4j;
import org.art.vertex.domain.note.search.model.EmbeddingModel;
import org.art.vertex.infrastructure.embedding.EmbeddingProviderFactory;
import org.art.vertex.test.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class EmbeddingIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private EmbeddingProviderFactory embeddingProviderFactory;

    @Test
    @DisplayName("Should generate valid embeddings using Ollama nomic-embed-text model")
    void shouldGenerateValidEmbeddings() {
        // GIVEN
        var provider = embeddingProviderFactory.getProvider(
            EmbeddingModel.OLLAMA_NOMIC_EMBED_TEXT_SMALL
        );

        assertThat(provider).isNotNull();
        assertThat(provider.isReady()).isTrue();
        assertThat(provider.getDimension()).isEqualTo(768);

        // WHEN
        var sampleText = "Machine learning is a subset of artificial intelligence";
        var embedding = provider.embed(sampleText);

        // THEN
        assertThat(embedding)
            .as("Embedding should not be null")
            .isNotNull();

        assertThat(embedding)
            .as("Embedding should have 768 dimensions")
            .hasSize(768);

        assertThat(embedding)
            .as("Embedding should not contain null values")
            .doesNotContainNull();

        assertThat(embedding)
            .as("Embedding should not be all zeros")
            .anyMatch(value -> Math.abs(value) > 0.0001f);

        var magnitude = calculateMagnitude(embedding);
        assertThat(magnitude)
            .as("Embedding should be L2 normalized (magnitude H 1.0)")
            .isCloseTo(1.0, org.assertj.core.data.Offset.offset(0.01));
    }

    @Test
    @DisplayName("Should produce similar embeddings for semantically similar texts")
    void shouldProduceSimilarEmbeddingsForSimilarTexts() {
        // GIVEN
        var provider = embeddingProviderFactory.getProvider(
            EmbeddingModel.OLLAMA_NOMIC_EMBED_TEXT_SMALL
        );

        var text1 = "Machine learning is a subset of artificial intelligence";
        var text2 = "Artificial intelligence includes machine learning as a component";

        // WHEN
        var embedding1 = provider.embed(text1);
        var embedding2 = provider.embed(text2);

        // THEN
        var similarity = calculateCosineSimilarity(embedding1, embedding2);

        assertThat(similarity)
            .as("Cosine similarity between semantically similar texts should be high")
            .isGreaterThan(0.7);
    }

    // ========== Helper Methods ==========

    /**
     * Calculate L2 norm (magnitude) of a vector.
     */
    private double calculateMagnitude(List<Float> vector) {
        double sumSquares = 0.0;
        for (Float value : vector) {
            sumSquares += value * value;
        }
        return Math.sqrt(sumSquares);
    }

    /**
     * Calculate cosine similarity between two vectors.
     * Returns a value between -1 (opposite) and 1 (identical).
     */
    private double calculateCosineSimilarity(List<Float> vector1, List<Float> vector2) {
        if (vector1.size() != vector2.size()) {
            throw new IllegalArgumentException("Vectors must have same dimensions");
        }

        double dotProduct = 0.0;
        double magnitude1 = 0.0;
        double magnitude2 = 0.0;

        for (int i = 0; i < vector1.size(); i++) {
            dotProduct += vector1.get(i) * vector2.get(i);
            magnitude1 += vector1.get(i) * vector1.get(i);
            magnitude2 += vector2.get(i) * vector2.get(i);
        }

        magnitude1 = Math.sqrt(magnitude1);
        magnitude2 = Math.sqrt(magnitude2);

        if (magnitude1 == 0.0 || magnitude2 == 0.0) {
            return 0.0;
        }

        return dotProduct / (magnitude1 * magnitude2);
    }
}
