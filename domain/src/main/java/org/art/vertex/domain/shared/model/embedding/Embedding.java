package org.art.vertex.domain.shared.model.embedding;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Value
@Builder
public class Embedding {

    UUID id;

    @Builder.Default
    List<Float> vector = List.of();

    int dimension;

    String model;

    LocalDateTime createdAt;
}