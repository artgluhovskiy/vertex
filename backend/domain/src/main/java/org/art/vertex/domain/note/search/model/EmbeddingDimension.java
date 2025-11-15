package org.art.vertex.domain.note.search.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum EmbeddingDimension {
    SMALL(768),
    MEDIUM(1024),
    LARGE(1536);

    private final int dim;

    /**
     * Get EmbeddingDimension from dimension value.
     *
     * @param dimension dimension size (768, 1024, or 1536)
     * @return corresponding EmbeddingDimension
     * @throws IllegalArgumentException if dimension is not supported
     */
    public static EmbeddingDimension fromValue(int dimension) {
        return Arrays.stream(values())
            .filter(d -> d.dim == dimension)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                "Unsupported embedding dimension: " + dimension +
                ". Supported dimensions: 768 (SMALL), 1024 (MEDIUM), 1536 (LARGE)"));
    }

    /**
     * Check if dimension value is supported.
     *
     * @param dimension dimension size to check
     * @return true if supported, false otherwise
     */
    public static boolean isSupported(int dimension) {
        return Arrays.stream(values())
            .anyMatch(d -> d.dim == dimension);
    }
}
