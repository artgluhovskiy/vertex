package org.art.vertex.domain.shared;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VectorUtils {

    /**
     * Convert list of Double to list of Float.
     * Ollama returns doubles, but we use floats for efficiency.
     *
     * @param doubles List of doubles from Ollama
     * @return List of floats
     */
    public static List<Float> convertToFloatList(List<Double> doubles) {
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
    public static void normalizeVector(List<Float> vector) {
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
