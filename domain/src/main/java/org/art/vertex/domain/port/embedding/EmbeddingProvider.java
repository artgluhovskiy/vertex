package org.art.vertex.domain.port.embedding;

import org.art.vertex.domain.model.embedding.Embedding;
import org.art.vertex.domain.model.embedding.EmbeddingModel;

import java.util.List;

public interface EmbeddingProvider {

    boolean supports(String provider);

    Embedding generate(String text, EmbeddingModel model);

    List<Embedding> generateBatch(List<String> texts, EmbeddingModel model);
}