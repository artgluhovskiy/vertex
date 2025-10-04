package org.art.vertex.domain.shared.port.embedding;

import org.art.vertex.domain.shared.model.embedding.Embedding;
import org.art.vertex.domain.shared.model.embedding.EmbeddingModel;

import java.util.List;

public interface EmbeddingProvider {

    boolean supports(String provider);

    Embedding generate(String text, EmbeddingModel model);

    List<Embedding> generateBatch(List<String> texts, EmbeddingModel model);
}