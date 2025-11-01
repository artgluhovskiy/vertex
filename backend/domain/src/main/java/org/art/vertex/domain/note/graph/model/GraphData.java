package org.art.vertex.domain.note.graph.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
@Builder
public class GraphData {

    @Builder.Default
    List<GraphNode> nodes = List.of();

    @Builder.Default
    List<GraphEdge> edges = List.of();

    @Builder.Default
    Map<String, Object> metadata = Map.of();
}