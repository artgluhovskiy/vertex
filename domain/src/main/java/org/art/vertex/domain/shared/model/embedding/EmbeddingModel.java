package org.art.vertex.domain.shared.model.embedding;

import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Value
@Builder
public class EmbeddingModel {

    String name;

    String provider;

    int dimension;

    @Builder.Default
    Map<String, Object> config = Map.of();
}