package org.art.vertex.domain.model.graph;

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