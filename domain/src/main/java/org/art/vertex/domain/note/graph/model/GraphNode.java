package org.art.vertex.domain.note.graph.model;

import lombok.Builder;
import lombok.Value;

import java.util.Map;
import java.util.UUID;

@Value
@Builder
public class GraphNode {

    UUID id;

    String label;

    String type;

    @Builder.Default
    Map<String, Object> properties = Map.of();
}