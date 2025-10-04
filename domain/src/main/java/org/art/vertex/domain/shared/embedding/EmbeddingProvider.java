package org.art.vertex.domain.shared.embedding;

import java.util.List;

public interface EmbeddingProvider {

    boolean supports(String provider);

    Embedding generate(String text, EmbeddingModel model);

    List<Embedding> generateBatch(List<String> texts, EmbeddingModel model);
}